package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDAO {

    Item createItem(Item item);
    Item updateItem(Item item);
    Item getItemById(Long itemId);
    List<Item> getAllItemsOfUser(Long userId);
    List<Item> searchItem(String searchQuery);

}
