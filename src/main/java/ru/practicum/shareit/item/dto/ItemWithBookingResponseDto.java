package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingShortInfo;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ItemWithBookingResponseDto {
    private Long id;
    private String name;
    private String description;
    @JsonProperty("available")
    private Boolean isAvailable;
    private BookingShortInfo lastBooking;
    private BookingShortInfo nextBooking;
    private List<CommentDto> comments;

}
