package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import java.util.List;

public interface BookingService {
    ResponseBookingDto create(RequestBookingDto bookingDtoShort, Long bookerId);
    ResponseBookingDto findBookingById(Long bookingId, Long userId);
    List<ResponseBookingDto> findAllBookingsByUser(String state, Long userId);
    List<ResponseBookingDto> findAllBookingsByOwner(String state, Long ownerId);
    ResponseBookingDto approve(Long bookingId, Long userId, Boolean isApproved);
}
