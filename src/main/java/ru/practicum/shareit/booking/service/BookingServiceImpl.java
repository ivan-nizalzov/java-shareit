package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserServiceImpl userServiceImpl;
    private final ItemServiceImpl itemServiceImpl;
    private final BookingMapper bookingMapper;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    @Transactional
    @Override
    public ResponseBookingDto create(RequestBookingDto bookingDtoShort, Long bookerId) {
        if (bookingDtoShort.getEnd().isBefore(bookingDtoShort.getStart()) ||
                bookingDtoShort.getEnd().equals(bookingDtoShort.getStart())) {
            log.warn("Invalid booking time start={} end={}", bookingDtoShort.getStart(), bookingDtoShort.getEnd());
            throw new TimeDataException(String.format("Invalid booking time start = %s  end = %s",
                            bookingDtoShort.getStart(), bookingDtoShort.getEnd()));
        }

        User booker = userMapper.toUser(userServiceImpl.findUserById(bookerId));
        Item item = itemMapper.toItem(itemServiceImpl.findItemById(bookingDtoShort.getItemId(), bookerId));

        if (itemServiceImpl.findOwnerId(item.getId()).equals(bookerId)) {
            log.warn("User with id={} cannot be a booker (User is the owner).", bookerId);
            throw new ForbiddenAccessException("The owner cannot be a booker.");
        }

        if (item.getAvailable()) {
            Booking booking = Booking.builder()
                    .start(bookingDtoShort.getStart())
                    .end(bookingDtoShort.getEnd())
                    .item(item)
                    .booker(booker)
                    .status(BookingStatus.WAITING)
                    .build();
            log.info("Created Booking with id={}.", booking.getId());
            return bookingMapper.toBookingDto(bookingRepository.save(booking));
        } else {
            log.warn("Item with id={} is not available.", item.getId());
            throw new NotAvailableException("Item with id = %d is not available.");
        }
    }

    @Transactional
    @Override
    public ResponseBookingDto findBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException(String.format("Booking with id = %d not found.", bookingId)));

        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            log.info("Found Booking with id={}.", bookingId);
            return bookingMapper.toBookingDto(booking);
        } else {
            log.warn("User with id={} nas no access to Booking with id={} (User isn't the owner).", userId, bookingId);
            throw new ForbiddenAccessException(String.format("User with id = %d is not the owner, " +
                            "access to booking is denied.",
                    userId));
        }
    }

    @Transactional
    @Override
    public List<ResponseBookingDto> findAllBookingsByUser(String state, Long userId, Integer from, Integer size) {
        userServiceImpl.findUserById(userId);
        checkPageRequest(from, size);
        Pageable page = PageRequest.of(from / size, size);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case "ALL":
                log.info("Found all bookings with state 'ALL' made by User with id={}.", userId);
                return bookingMapper.toBookingDto(bookingRepository.findAllByBookerIdOrderByStartDesc(userId, page));
            case "CURRENT":
                log.info("Found all bookings with state 'CURRENT' made by User with id={}.", userId);
                return bookingMapper.toBookingDto(bookingRepository
                        .findAllByBookerIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(userId, now, now, page));
            case "PAST":
                log.info("Found all bookings with state 'PAST' made by User with id={}.", userId);
                return bookingMapper.toBookingDto(bookingRepository
                        .findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, now, page));
            case "FUTURE":
                log.info("Found all bookings with state 'FUTURE' made by User with id={}.", userId);
                return bookingMapper.toBookingDto(bookingRepository
                        .findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, now, page));
            case "WAITING":
                log.info("Found all bookings with state 'WAITING' made by User with id={}.", userId);
                return bookingMapper.toBookingDto(bookingRepository
                        .findAllByBookerIdAndStartIsAfterAndStatusIsOrderByStartDesc(userId, now,
                                BookingStatus.WAITING, page));
            case "REJECTED":
                log.info("Found all bookings with state 'REJECTED' made by User with id={}.", userId);
                return bookingMapper.toBookingDto(bookingRepository
                        .findAllByBookerIdAndStatusIsOrderByStartDesc(userId, BookingStatus.REJECTED, page));

        }
        log.warn("Unknown state: {}.", state);
        throw new BadRequestException(String.format("Unknown state: %s", state));
    }

    @Transactional
    @Override
    public List<ResponseBookingDto> findAllBookingsByOwner(String state, Long ownerId, Integer from, Integer size) {
        userServiceImpl.findUserById(ownerId);
        checkPageRequest(from, size);
        Pageable page = PageRequest.of(from / size, size);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case "ALL":
                log.info("Found all bookings with state 'ALL' of Owner with id={}.", ownerId);
                return bookingMapper.toBookingDto(bookingRepository.findAllBookingsOwner(ownerId, page));
            case "CURRENT":
                log.info("Found all bookings with state 'CURRENT' of Owner with id={}.", ownerId);
                return bookingMapper.toBookingDto(bookingRepository.findAllCurrentBookingsOwner(ownerId, now, page));
            case "PAST":
                log.info("Found all bookings with state 'PAST' of Owner with id={}.", ownerId);
                return bookingMapper.toBookingDto(bookingRepository.findAllPastBookingsOwner(ownerId, now, page));
            case "FUTURE":
                log.info("Found all bookings with state 'FUTURE' of Owner with id={}.", ownerId);
                return bookingMapper.toBookingDto(bookingRepository.findAllFutureBookingsOwner(ownerId, now, page));
            case "WAITING":
                log.info("Found all bookings with state 'WAITING' of Owner with id={}.", ownerId);
                return bookingMapper.toBookingDto(bookingRepository
                        .findAllWaitingBookingsOwner(ownerId, now, BookingStatus.WAITING, page));
            case "REJECTED":
                log.info("Found all bookings with state 'REJECTED' of Owner with id={}.", ownerId);
                return bookingMapper.toBookingDto(bookingRepository
                        .findAllRejectedBookingsOwner(ownerId, BookingStatus.REJECTED, page));
        }
        log.warn("Unknown state: {}.", state);
        throw new BadRequestException(String.format("Unknown state: %s", state));
    }

    @Transactional
    @Override
    public ResponseBookingDto approve(Long bookingId, Long userId, Boolean isApproved) {
        ResponseBookingDto booking = findBookingById(bookingId, userId);
        Long ownerId = itemServiceImpl.findOwnerId(booking.getItem().getId());

        if (ownerId.equals(userId) && booking.getStatus().equals(BookingStatus.APPROVED)) {
            log.warn("The booking decision has already been made.");
            throw new AlreadyExistsException("The booking decision has already been made.");
        }
        if (!ownerId.equals(userId)) {
            log.warn("User with id={} is not the owner.", userId);
            throw new ForbiddenAccessException(String.format("User with id = %d is not the owner, no access to booking.",
                    userId));
        }

        if (isApproved) {
            booking.setStatus(BookingStatus.APPROVED);
            bookingRepository.update(BookingStatus.APPROVED, bookingId);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
            bookingRepository.update(BookingStatus.REJECTED, bookingId);
        }
        log.info("Booking with id={} is approved by User with id={}.", bookingId, userId);

        return booking;
    }

    private void checkPageRequest(Integer from, Integer size) {
        if (from < 0) {
            throw new BadRequestException("Bad request: PageRequest 'from' cannot be less than one");
        }
    }

}
