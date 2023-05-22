package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long itemId;
    @Future
    @NotNull
    private LocalDateTime start;
    @Future
    @NotNull
    private LocalDateTime end;

}