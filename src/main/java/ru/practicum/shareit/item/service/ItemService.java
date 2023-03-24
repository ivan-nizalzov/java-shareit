package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userId);
    ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto);
    ItemDto getItemById(Long itemId);
    List<ItemDto> getAllItemsOfUser(Long userId);
    List<ItemDto> searchItem(String searchQuery);

}
