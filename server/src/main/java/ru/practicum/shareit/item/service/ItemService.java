package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(Long id, ItemDto itemDto);

    ItemDto update(Long idUser, Long id, ItemDto itemDto);

    ItemDto findById(Long idUser, Long id);

    List<ItemDto> findAllItemsOfUser(Long idUser, Integer from, Integer size);

    List<ItemDto> search(String text, Integer from, Integer size);

    CommentDto addComment(Long idUser, Long idItem, CommentDto commentDto);

}
