package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

import static ru.practicum.shareit.user.util.UserHeader.USER_HEADER;

@Validated
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@RequestHeader(USER_HEADER) Long userId,
                                                    @RequestBody BookingShortDto bookingDto) {

        return ResponseEntity.ok(bookingService.create(userId, bookingDto));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approveBooking(@RequestHeader(USER_HEADER) Long userId,
                                                     @PathVariable Long bookingId,
                                                     @RequestParam(name = "approved") Boolean approved) {

        return ResponseEntity.ok(bookingService.approve(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> findById(@RequestHeader(USER_HEADER) Long userId,
                                               @PathVariable Long bookingId) {

        return ResponseEntity.ok(bookingService.findById(userId, bookingId));
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> findAllBookingsMadeByUser(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size) {

        return ResponseEntity.ok(bookingService.findAllBookingsMadeByUser(userId, state, from, size));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> findAllBookingsOfItemsOwner(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size) {

        return ResponseEntity.ok(bookingService.findAllBookingsOfItems(userId, state, from, size));
    }

}
