package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

public interface BookingService {

    BookingDto create(Long userId, BookingShortDto bookingDto);

    BookingDto approve(Long userId, Long bookingId, Boolean approved);

    BookingDto findById(Long userId, Long bookingId);

    List<BookingDto> findAllBookingsMadeByUser(Long userId, String text, Integer from, Integer size);

    List<BookingDto> findAllBookingsOfItems(Long userId, String text, Integer from, Integer size);

}
