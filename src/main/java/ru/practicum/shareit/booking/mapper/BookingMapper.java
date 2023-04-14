package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingShortInfo;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "item.item_id", source = "itemId")
    Booking convertDtoToBooking(BookingRequestDto bookingRequestDto);

    //@Mapping(target = "item.name", source = "")
    BookingResponseDto convertBookingToDto(Booking booking);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "item", ignore = true)
    BookingShortInfo convertToBookingShortInfo(Booking booking);

}
