package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item createItem(Item item);
    Item updateItem(Item item);
    Item getItemById(Long itemId);
    List<Item> getAllItemsOfUser(Long userId);
    List<Item> searchItem(String searchQuery);

}
