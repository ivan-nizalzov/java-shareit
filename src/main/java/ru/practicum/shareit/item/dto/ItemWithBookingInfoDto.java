package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.model.BookingShortInfo;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class ItemWithBookingInfoDto {

  private Long id;
  private String name;
  private String description;
  @JsonProperty("available")
  private Boolean isAvailable;
  private BookingShortInfo lastBooking;
  private BookingShortInfo nextBooking;
  private List<CommentDto> comments;

  public ItemWithBookingInfoDto(Long id, String name, String description, Boolean isAvailable) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.isAvailable = isAvailable;
  }
}
