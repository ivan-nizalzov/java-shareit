package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingCreateResponseDto {

  private Long id;
  @JsonProperty("start")
  private LocalDateTime startDateTime;
  @JsonProperty("end")
  private LocalDateTime endDateTime;
  private Item item;
  private User booker;
  private BookingStatus status;
}
