package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.UnsupportedStatus;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import static ru.practicum.shareit.user.util.UserHeader.USER_HEADER;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(USER_HEADER) Long userId,
                                                   @Valid @RequestBody BookingDto bookingDto) {

        return ResponseEntity.ok(bookingClient.bookItem(userId, bookingDto));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader(USER_HEADER) Long userId,
                                     @PathVariable Long bookingId,
                                     @RequestParam(name = "approved") Boolean approved) {

        return ResponseEntity.ok(bookingClient.approve(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findById(@RequestHeader(USER_HEADER) Long userId,
                               @PathVariable Long bookingId) {

        return ResponseEntity.ok(bookingClient.findById(userId, bookingId));
    }

    @GetMapping
    public ResponseEntity<Object> findAllBookingsMadeByUser(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestParam(required = false, defaultValue = "ALL") String stateParam,
            @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
            @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {

        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new UnsupportedStatus("Unknown state: " + stateParam));

        return ResponseEntity.ok(bookingClient.findAllBookingsMadeByUser(userId, state, from, size));
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllBookingsOfItemsOwner(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestParam(required = false, defaultValue = "ALL") String stateParam,
            @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
            @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {

        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new UnsupportedStatus("Unknown state: " + stateParam));

        return ResponseEntity.ok(bookingClient.findAllBookingsOfItemsOwner(userId, state, from, size));
    }
}
