package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<ResponseBookingDto> create(
            @RequestHeader(USER_HEADER) Long userId,
            @Valid @RequestBody RequestBookingDto bookingDtoShort) {
        log.debug("POST /bookings : create booking");
        return ResponseEntity.ok((bookingService.create(bookingDtoShort, userId)));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<ResponseBookingDto> findById(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable Long bookingId) {
        log.debug("GET /bookings/{bookingId} : get booking by id");
        return ResponseEntity.ok(bookingService.findBookingById(bookingId, userId));
    }

    @GetMapping
    public ResponseEntity<List<ResponseBookingDto>> findAllByUserId(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.debug("GET /bookings : get all bookings of user");
        return ResponseEntity.ok(bookingService.findAllBookingsByUser(state, userId, from, size));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<ResponseBookingDto>> findAllByOwnerId(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.debug("GET /bookings/owner : get all bookings of owner");
        return ResponseEntity.ok(bookingService.findAllBookingsByOwner(state, userId, from, size));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<ResponseBookingDto> update(@RequestHeader(USER_HEADER) Long userId,
                                                     @PathVariable Long bookingId,
                                                     @RequestParam Boolean approved) {
        log.debug("PATCH /bookings/{bookingId} : update booking");
        return ResponseEntity.ok(bookingService.approve(bookingId, userId, approved));
    }

}