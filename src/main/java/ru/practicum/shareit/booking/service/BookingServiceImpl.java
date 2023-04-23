package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Transactional
    @Override
    public ResponseBookingDto create(RequestBookingDto bookingDtoShort, Long bookerId) {
        if (bookingDtoShort.getEnd().isBefore(bookingDtoShort.getStart()) ||
                bookingDtoShort.getEnd().equals(bookingDtoShort.getStart())) {
            log.warn("Invalid booking time start={} end={}", bookingDtoShort.getStart(), bookingDtoShort.getEnd());
            throw new TimeDataException(String.format("Invalid booking time start = %s  end = %s",
                            bookingDtoShort.getStart(), bookingDtoShort.getEnd()));
        }

        User booker = UserMapper.toUser(userServiceImpl.findUserById(bookerId));
        Item item = ItemMapper.toItem(itemServiceImpl.findItemById(bookingDtoShort.getItemId(), bookerId));

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
            return BookingMapper.toBookingDto(bookingRepository.save(booking));
        } else {
            log.warn("Item with id={} is not available.", item.getId());
            throw new NotAvailableException("Item with id = %d is not available.");
        }
    }

    @Transactional
    @Override
    public ResponseBookingDto findBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException(String.format("Booking with id = %d is not found.", bookingId)));

        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwnerId().equals(userId)) {
            log.info("Found Booking with id={}.", bookingId);
            return BookingMapper.toBookingDto(booking);
        } else {
            log.warn("User with id={} nas no access to Booking with id={} (User isn't the owner).", userId, bookingId);
            throw new ForbiddenAccessException(String.format("User with id = %d is not the owner, " +
                            "access to booking is denied.",
                    userId));
        }
    }

    @Transactional
    @Override
    public List<ResponseBookingDto> findAllBookingsByUser(String state, Long userId) {
        userServiceImpl.findUserById(userId);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case "ALL":
                log.info("Found all bookings with state 'ALL' made by User with id={}.", userId);
                return BookingMapper.toBookingDto(bookingRepository.findAllByBookerIdOrderByStartDesc(userId));
            case "CURRENT":
                log.info("Found all bookings with state 'CURRENT' made by User with id={}.", userId);
                return BookingMapper.toBookingDto(bookingRepository
                        .findAllByBookerIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(userId, now, now));
            case "PAST":
                log.info("Found all bookings with state 'PAST' made by User with id={}.", userId);
                return BookingMapper.toBookingDto(bookingRepository
                        .findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, now));
            case "FUTURE":
                log.info("Found all bookings with state 'FUTURE' made by User with id={}.", userId);
                return BookingMapper.toBookingDto(bookingRepository
                        .findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, now));
            case "WAITING":
                log.info("Found all bookings with state 'WAITING' made by User with id={}.", userId);
                return BookingMapper.toBookingDto(bookingRepository
                        .findAllByBookerIdAndStartIsAfterAndStatusIsOrderByStartDesc(userId, now,
                                BookingStatus.WAITING));
            case "REJECTED":
                log.info("Found all bookings with state 'REJECTED' made by User with id={}.", userId);
                return BookingMapper.toBookingDto(bookingRepository
                        .findAllByBookerIdAndStatusIsOrderByStartDesc(userId, BookingStatus.REJECTED));

        }
        log.warn("Unknown state: {}.", state);
        throw new BadRequestException(String.format("Unknown state: %s", state));
    }

    @Transactional
    @Override
    public List<ResponseBookingDto> findAllBookingsByOwner(String state, Long ownerId) {
        userServiceImpl.findUserById(ownerId);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case "ALL":
                log.info("Found all bookings with state 'ALL' of Owner with id={}.", ownerId);
                return BookingMapper.toBookingDto(bookingRepository.findAllBookingsOwner(ownerId));
            case "CURRENT":
                log.info("Found all bookings with state 'CURRENT' of Owner with id={}.", ownerId);
                return BookingMapper.toBookingDto(bookingRepository.findAllCurrentBookingsOwner(ownerId, now));
            case "PAST":
                log.info("Found all bookings with state 'PAST' of Owner with id={}.", ownerId);
                return BookingMapper.toBookingDto(bookingRepository.findAllPastBookingsOwner(ownerId, now));
            case "FUTURE":
                log.info("Found all bookings with state 'FUTURE' of Owner with id={}.", ownerId);
                return BookingMapper.toBookingDto(bookingRepository.findAllFutureBookingsOwner(ownerId, now));
            case "WAITING":
                log.info("Found all bookings with state 'WAITING' of Owner with id={}.", ownerId);
                return BookingMapper.toBookingDto(bookingRepository
                        .findAllWaitingBookingsOwner(ownerId, now, BookingStatus.WAITING));
            case "REJECTED":
                log.info("Found all bookings with state 'REJECTED' of Owner with id={}.", ownerId);
                return BookingMapper.toBookingDto(bookingRepository
                        .findAllRejectedBookingsOwner(ownerId, BookingStatus.REJECTED));
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
}
