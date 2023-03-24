package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    @JsonProperty("available")
    private Boolean isAvailable;

}
