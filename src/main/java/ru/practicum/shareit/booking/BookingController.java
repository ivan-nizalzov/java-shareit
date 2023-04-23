package ru.practicum.shareit.booking;

import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingCreateResponseDto;
import ru.practicum.shareit.booking.model.BookingFilter;
import ru.practicum.shareit.booking.service.BookingService;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

  private final BookingService bookingService;

  @PostMapping
  public BookingCreateResponseDto bookItem(@RequestHeader("X-Sharer-User-Id") long bookerId,
      @Valid @RequestBody BookingCreateRequestDto createRequestDto) {
    return bookingService.bookItem(bookerId, createRequestDto);
  }

  @PatchMapping("/{bookingId}")
  public BookingCreateResponseDto decidingOnRequest(@RequestHeader("X-Sharer-User-Id") long userId,
      @PathVariable long bookingId,
      @RequestParam("approved") boolean isApproved) {
    return bookingService.decidingOnRequest(userId, bookingId, isApproved);
  }

  @GetMapping("/{bookingId}")
  public BookingCreateResponseDto getBookingInfo(@RequestHeader("X-Sharer-User-Id") long userId,
      @PathVariable long bookingId) {
    return bookingService.getBookingInfo(userId, bookingId);
  }

  @GetMapping
  public List<BookingCreateResponseDto> getAllBookingInfo(@RequestHeader("X-Sharer-User-Id") long userId,
      @RequestParam(required = false, defaultValue = "ALL") BookingFilter state) {
    return bookingService.getAllBookingInfo(userId, state);
  }

  @GetMapping("/owner")
  public List<BookingCreateResponseDto> getAllOwnerBookingInfo(@RequestHeader("X-Sharer-User-Id") long userId,
      @RequestParam(required = false, defaultValue = "ALL") BookingFilter state) {
    return bookingService.getAllOwnerBookingInfo(userId, state);
  }
}
