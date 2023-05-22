package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.TimeDataException;
import ru.practicum.shareit.exception.UnsupportedStatus;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    public final BookingRepository bookingRepository;
    public final UserRepository userRepository;
    public final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Transactional
    public BookingDto create(Long userId, BookingShortDto bookingDto) {
        checkBookingDate(bookingDto);

        User user = checkUserInDb(userId);
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item with id=" + bookingDto.getItemId() + " not found."));

        checkBookerIsOwner(userId, item);
        checkIsAvailableItem(item);

        Booking newBooking = bookingMapper.toBooking(bookingDto, user, item, BookingStatus.WAITING);
        Booking booking = bookingRepository.save(newBooking);

        log.info("Created booking with id={}.", booking.getId());

        return bookingMapper.toBookingDto(booking);
    }

    @Transactional
    public BookingDto approve(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id=" + bookingId + " not found."));

        if (booking.getStatus().equals(BookingStatus.WAITING)) {
            if (booking.getItem().getOwner().getId().equals(userId)) {
                if (approved) {
                    booking.setStatus(BookingStatus.APPROVED);
                } else {
                    booking.setStatus(BookingStatus.REJECTED);
                }
            } else {
                throw new NotFoundException("User with id=" + userId + " is not the owner of item with id="
                        + booking.getItem().getId());
            }
        } else {
            throw new BadRequestException("The decision has been already made.");
        }
        log.info("Approved booking with id={} by user with id={}.", bookingId, userId);
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    public BookingDto findById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id=" + bookingId + " not found."));

        if (booking.getItem().getOwner().getId().equals(userId) || booking.getBooker().getId().equals(userId)) {
            log.info("Found booking with id={} made by user with id={}.", bookingId, userId);
            return bookingMapper.toBookingDto(booking);
        } else throw new NotFoundException("The booker cannot be an owner.");
    }

    public List<BookingDto> findAllBookingsMadeByUser(Long userId, String stateText, Integer from, Integer size) {
        BookingState state = BookingState.getStateFromText(stateText);
        LocalDateTime dateTime = LocalDateTime.now();
        List<Booking> bookingList;
        User user = checkUserInDb(userId);
        int start = from / size;
        PageRequest page = PageRequest.of(start, size);

        switch (state) {
            case ALL:
                bookingList = bookingRepository.getAllBookingsById(userId, page);
                log.info("Found all bookings with state 'ALL' made by user with id={}.", userId);
                break;
            case CURRENT:
                bookingList = bookingRepository.findDByBookerAndStartBeforeAndEndAfterOrderByStartDesc(
                        user, dateTime, dateTime, page);
                log.info("Found all bookings with state 'CURRENT' made by user with id={}.", userId);
                break;
            case FUTURE:
                bookingList = bookingRepository.findDByBookerAndStartAfterOrderByStartDesc(user, dateTime, page);
                log.info("Found all bookings with state 'FUTURE' made by user with id={}.", userId);
                break;
            case PAST:
                bookingList = bookingRepository.findDByBookerAndEndBeforeOrderByStartDesc(user, dateTime, page);
                log.info("Found all bookings with state 'PAST' made by user with id={}.", userId);
                break;
            case WAITING:
                bookingList = bookingRepository.findDByBookerAndStatusOrderByStartDesc(
                        user, BookingStatus.WAITING, page);
                log.info("Found all bookings with state 'WAITING' made by user with id={}.", userId);
                break;
            case REJECTED:
                bookingList = bookingRepository.findDByBookerAndStatusOrderByStartDesc(
                        user, BookingStatus.REJECTED, page);
                log.info("Found all bookings with state 'REJECTED' made by user with id={}.", userId);
                break;
            default:
                throw new UnsupportedStatus("Unsupported status (unknown state of booking).");
        }

        return bookingList.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    public List<BookingDto> findAllBookingsOfItems(Long userId, String text, Integer start, Integer size) {
        BookingState state = BookingState.getStateFromText(text);
        LocalDateTime dateTime = LocalDateTime.now();
        List<Booking> bookingList;
        checkUserInDb(userId);
        int from = start / size;
        PageRequest page = PageRequest.of(from, size);
        List<Item> items = itemRepository.findByOwnerId(userId);

        if (items.size() > 0) {
            switch (state) {
                case ALL:
                    bookingList = bookingRepository.findDByItemInOrderByStartDesc(items, page);
                    log.info("Found all bookings with state 'ALL' of their owner's items, ownerId={}.", userId);
                    break;
                case CURRENT:
                    bookingList = bookingRepository.findDByItemInAndStartBeforeAndEndAfterOrderByStartDesc(
                            items, dateTime, dateTime, page);
                    log.info("Found all bookings with state 'CURRENT' of their owner's items, ownerId={}.", userId);
                    break;
                case FUTURE:
                    bookingList = bookingRepository.findDByItemInAndStartAfterOrderByStartDesc(items, dateTime, page);
                    log.info("Found all bookings with state 'FUTURE' of their owner's items, ownerId={}.", userId);
                    break;
                case PAST:
                    bookingList = bookingRepository.findDByItemInAndEndBeforeOrderByStartDesc(items, dateTime, page);
                    log.info("Found all bookings with state 'PAST' of their owner's items, ownerId={}.", userId);
                    break;
                case WAITING:
                    bookingList = bookingRepository.findDByItemInAndStatusOrderByStartDesc(
                            items, BookingStatus.WAITING, page);
                    log.info("Found all bookings with state 'WAITING' of their owner's items, ownerId={}.", userId);
                    break;
                case REJECTED:
                    bookingList = bookingRepository.findDByItemInAndStatusOrderByStartDesc(
                            items, BookingStatus.REJECTED, page);
                    log.info("Found all bookings with state 'REJECTED' of their owner's items, ownerId={}.", userId);
                    break;
                default:
                    throw new UnsupportedStatus("Unknown state: " + state);
            }
            return bookingList.stream()
                    .map(bookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else throw new BadRequestException("У пользователя нет ни одной вещи!");
    }

    private void checkIsAvailableItem(Item item) {
        if (!item.getAvailable()) {
            throw new BadRequestException("Item with id=" + item.getId() + " is not available.");
        }
    }

    private void checkBookingDate(BookingShortDto bookingDto) {
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) ||
                bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new TimeDataException("Invalid booking start time and end time.");
        }
    }

    private User checkUserInDb(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " is not found."));
    }

    private void checkBookerIsOwner(Long userId, Item item) {
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("The owner cannot be a booker.");
        }
    }

}
