package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingShortInfo;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

  public static Booking toBooking(long bookerId, BookingCreateRequestDto requestDto) {
    return toBooking(bookerId, requestDto, BookingStatus.WAITING);
  }

  public static Booking toBooking(long bookerId, BookingCreateRequestDto requestDto, BookingStatus status) {
    return Booking.builder()
        .booker(new User(bookerId))
        .item(new Item(requestDto.getItemId()))
        .startDateTime(requestDto.getStartDateTime())
        .endDateTime(requestDto.getEndDateTime())
        .status(status)
        .build();
  }

  public static BookingCreateResponseDto toBookingCreateResponseDto(Booking booking) {
    return BookingCreateResponseDto.builder()
        .id(booking.getId())
        .booker(booking.getBooker())
        .item(booking.getItem())
        .startDateTime(booking.getStartDateTime())
        .endDateTime(booking.getEndDateTime())
        .status(booking.getStatus())
        .build();
  }

  public static BookingShortInfo toBookingShortInfo(Booking booking) {
    return booking == null
        ? null
        : BookingShortInfo.builder()
            .id(booking.getId())
            .bookerId(booking.getBooker().getId())
            .startDateTime(booking.getStartDateTime())
            .endDateTime(booking.getEndDateTime())
            .build();
  }
}
