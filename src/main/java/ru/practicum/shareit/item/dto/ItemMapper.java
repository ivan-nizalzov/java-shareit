package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

public class ItemMapper {

  public static ItemDto toItemDto(Item item) {
    return new ItemDto(
        item.getId(),
        item.getName(),
        item.getDescription(),
        item.getIsAvailable()
    );
  }

  public static Item toItem(ItemDto itemDto, long userId, ItemRequest request) {
    return new Item(
        itemDto.getId(),
        itemDto.getName(),
        itemDto.getDescription(),
        itemDto.getIsAvailable(),
        userId,
        null
    );
  }

  public static ItemWithBookingInfoDto toItemDtoWithBookingInfoDto(Item item) {
    return new ItemWithBookingInfoDto(
        item.getId(),
        item.getName(),
        item.getDescription(),
        item.getIsAvailable()
    );
  }
}
