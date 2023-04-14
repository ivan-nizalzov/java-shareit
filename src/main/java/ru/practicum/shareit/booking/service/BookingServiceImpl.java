package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingFilter;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    //TODO BookingService methods
    @Override
    public BookingResponseDto createBooking(Long bookerId, BookingRequestDto bookingRequestDto) {
        User booker = getUserById(bookerId);
        Item item = getItemById(bookingRequestDto.getItemId());

        if (!item.getIsAvailable()) {
            throw new ValidationException("Вещь с id=" + item.getId() + " не доступен для бронирования");
        }

        if (bookerId.equals(item.getOwner().getId())) {
            throw new NotFoundException("Пользователь с id=" + bookerId + " является владельцем вещи");
        }

        Booking booking = bookingMapper.convertDtoToBooking(bookingRequestDto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        Booking savedBooking = bookingRepository.save(booking);

        return bookingMapper.convertBookingToDto(savedBooking);
    }

    @Override
    public BookingResponseDto approveItemRequest(Long userId, Long bookingId, boolean isApproved) {
        Booking booking = getBookingById(bookingId);

        if (!userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("Пользователь с id=" + userId + " не является владельцем вещи");
        }

        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("Пользователь с id=" + userId + " уже одобрил бронирование вещи с id=" +
                    booking.getItem().getId());
        }

        booking.setStatus(isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking savedBooking = bookingRepository.save(booking);

        return bookingMapper.convertBookingToDto(savedBooking);
    }

    @Override
    public BookingResponseDto getBookingInfo(Long userId, Long bookingId) {
        Booking booking = getBookingById(bookingId);

        if (!userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("Пользователь с id=" + userId + " не имеет права доступа к просмотру" +
                    " информации о вещи");
        }

        return bookingMapper.convertBookingToDto(booking);
    }

    @Override
    public List<BookingResponseDto> getAllBookingInfo(Long userId, BookingFilter state) {
        List<Booking> bookings;
        checkUserInDb(userId);

        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrder_ByStartDateTime_Desc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllCurrentBookings_ByBookerId(userId, LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerId_AndEndDateTimeBeforeOrder_ByStartDateTime_Desc(userId,
                        LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerId_AndStartDateTimeAfterOrder_ByStartDateTime_Desc(userId,
                        LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerId_AndStatusOrder_ByStartDateTime_Desc(userId,
                        BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerId_AndStatusOrder_ByStartDateTime_Desc(userId,
                        BookingStatus.REJECTED);
                break;
            default:
                throw new ValidationException("Передано некорректное значение состояния (поле 'state' " +
                        "в эндпоинте GET /bookings?state={state})");
        }

        return bookings.stream()
                .map(bookingMapper::convertBookingToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getAllOwnerBookingInfo(Long userId, BookingFilter state) {
        checkUserInDb(userId);
        List<Booking> bookings;
        List<Long> userItems = itemRepository.findAllByOwnerId(userId).stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItemIdInOrder_ByStartDateTime_Desc(userItems);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllCurrentBookings_ByItemsIds(userItems, LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemIdIn_AndEndDateTimeBeforeOrder_ByStartDateTime_Desc(userItems,
                        LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemIdIn_AndStartDateTimeAfterOrder_ByStartDateTime_Desc(userItems,
                        LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemIdIn_AndStatusOrder_ByStartDateTime_Desc(userItems,
                        BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemIdIn_AndStatusOrder_ByStartDateTime_Desc(userItems,
                        BookingStatus.REJECTED);
                break;
            default:
                throw new ValidationException("Передано некорректное значение состояния (поле 'state' " +
                        "в эндпоинте GET /bookings/owner?state={state})");
        }

        return bookings.stream()
                .map(bookingMapper::convertBookingToDto)
                .collect(Collectors.toList());

    }

    private Booking getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Бронирование с id=" + bookingId + " не найдено в БД"));

        return booking;
    }

    private User getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id=" + userId + " не найден в БД"));

        return user;
    }

    private Item getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Вещь с id=" + itemId + " не найден в БД"));

        return item;
    }

    private void checkUserInDb(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден в БД");
        }
    }

}
