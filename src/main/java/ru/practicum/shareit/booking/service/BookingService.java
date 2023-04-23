package ru.practicum.shareit.booking.service;

import java.util.List;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingCreateResponseDto;
import ru.practicum.shareit.booking.model.BookingFilter;

public interface BookingService {

  BookingCreateResponseDto bookItem(long bookerId, BookingCreateRequestDto requestDto);

  BookingCreateResponseDto decidingOnRequest(long userId, long bookingId, boolean isApproved);

  BookingCreateResponseDto getBookingInfo(long userId, long bookingId);

  List<BookingCreateResponseDto> getAllBookingInfo(long userId, BookingFilter state);

  List<BookingCreateResponseDto> getAllOwnerBookingInfo(long userId, BookingFilter state);
}
