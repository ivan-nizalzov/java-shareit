package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingFilter;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingResponseDto bookItem(@RequestHeader(USER_HEADER) long bookerId,
                                       @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        return bookingService.createBooking(bookerId, bookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveItemRequest(@RequestHeader(USER_HEADER) long userId,
                                                 @PathVariable long bookingId,
                                                 @RequestParam("approved") boolean isApproved) {
        return bookingService.approveItemRequest(userId, bookingId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingInfo(@RequestHeader(USER_HEADER) long userId,
                                             @PathVariable long bookingId) {
        return bookingService.getBookingInfo(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getAllBookingInfo(@RequestHeader(USER_HEADER) long userId,
                                                      @RequestParam(required = false, defaultValue = "ALL")
                                                            BookingFilter state) {
        return bookingService.getAllBookingInfo(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllOwnerBookingInfo(@RequestHeader(USER_HEADER) long userId,
                                                           @RequestParam(required = false, defaultValue = "ALL")
                                                                 BookingFilter state) {
        return bookingService.getAllOwnerBookingInfo(userId, state);
    }

}
