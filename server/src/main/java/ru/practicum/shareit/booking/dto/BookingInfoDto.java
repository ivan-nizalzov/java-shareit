package ru.practicum.shareit.booking.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingInfoDto {
    private Long id;
    private Long bookerId;
}
