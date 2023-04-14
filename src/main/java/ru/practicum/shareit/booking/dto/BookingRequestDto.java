package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Future;
import java.time.LocalDateTime;

@Getter
@Setter
public class BookingRequestDto {
    private Long itemId;
    @Future
    @JsonProperty("start")
    private LocalDateTime startDateTime;
    @Future
    @JsonProperty("end")
    private LocalDateTime endDateTime;

}
