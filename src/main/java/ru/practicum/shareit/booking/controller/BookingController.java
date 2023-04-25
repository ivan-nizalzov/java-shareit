package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<ResponseBookingDto> create(@RequestHeader(USER_HEADER) long userId,
                                                     @Valid @RequestBody RequestBookingDto bookingDtoShort) {
        return ResponseEntity.ok((bookingService.create(bookingDtoShort, userId)));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<ResponseBookingDto> findById(@RequestHeader(USER_HEADER) Long userId,
                                                       @PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.findBookingById(bookingId, userId));
    }

    @GetMapping
    public ResponseEntity<List<ResponseBookingDto>> findAllByUserId(@RequestHeader(USER_HEADER) Long userId,
                                                                    @RequestParam(defaultValue = "ALL") String state) {
        return ResponseEntity.ok(bookingService.findAllBookingsByUser(state, userId));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<ResponseBookingDto>> findAllByOwnerId(@RequestHeader(USER_HEADER) Long userId,
                                                                     @RequestParam(defaultValue = "ALL") String state) {
        return ResponseEntity.ok(bookingService.findAllBookingsByOwner(state, userId));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<ResponseBookingDto> update(@RequestHeader(USER_HEADER) Long userId,
                                                     @PathVariable Long bookingId,
                                                     @RequestParam Boolean approved) {
        return ResponseEntity.ok(bookingService.approve(bookingId, userId, approved));
    }

}