package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingResponseDto {
    private Long id;
    @JsonProperty("start")
    private LocalDateTime startDateTime;
    @JsonProperty("end")
    private LocalDateTime endDateTime;
    private Item item;
    private User booker;
    private BookingStatus status;
}
