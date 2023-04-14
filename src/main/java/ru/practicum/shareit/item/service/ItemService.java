package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingResponseDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userId);
    ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto);
    //ItemDto getItemById(Long itemId);
    ItemWithBookingResponseDto getItemById(Long itemId, Long userId);
    List<ItemWithBookingResponseDto> getAllItemsOfUser(Long userId);
    List<ItemDto> searchItem(String searchQuery);
    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);

}
