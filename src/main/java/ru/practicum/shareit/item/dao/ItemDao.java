package ru.practicum.shareit.item.dao;

import java.util.List;
import ru.practicum.shareit.item.model.Item;

public interface ItemDao {

  Item createItem(Item item);

  Item updateItem(Item item);

  Item getItem(Long itemId);

  List<Item> getItems(Long ownerId);

  List<Item> searchItem(String searchCriteria);
}
