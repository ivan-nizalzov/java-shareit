package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingFilter;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(Long bookerId, BookingRequestDto bookingRequestDto);
    BookingResponseDto approveItemRequest(Long userId, Long bookingId, boolean isApproved);
    BookingResponseDto getBookingInfo(Long userId, Long bookingId);
    List<BookingResponseDto> getAllBookingInfo(Long userId, BookingFilter state);
    List<BookingResponseDto> getAllOwnerBookingInfo(Long userId, BookingFilter state);
}
