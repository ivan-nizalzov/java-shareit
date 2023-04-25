package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.dto.ShortItemBookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    ResponseBookingDto toBookingDto(Booking booking);

    List<ResponseBookingDto> toBookingDto(List<Booking> bookings);

    @Mapping(source = "booker.id", target = "bookerId")
    ShortItemBookingDto toItemBookingDto(Booking booking);

}