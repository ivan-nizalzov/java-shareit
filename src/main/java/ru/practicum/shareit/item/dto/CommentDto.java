package ru.practicum.shareit.item.dto;

import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CommentDto {

  private Long id;
  @NotBlank
  private String text;
  private String authorName;
  private LocalDateTime created;
}
