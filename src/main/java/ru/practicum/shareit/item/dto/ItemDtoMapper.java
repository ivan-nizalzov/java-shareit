package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

public class ItemDtoMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable()
        );
    }

    public static Item dtoToItem(ItemDto itemDto, Long userId, ItemRequest itemRequest) {
        Item item = Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .isAvailable(itemDto.getIsAvailable())
                .ownerId(userId)
                .request(itemRequest)
                .build();

        return item;
    }

}
