package ru.practicum.shareit.item.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

@Slf4j
@Component
public class ItemInMemoryDaoImpl implements ItemDao {

  private final Map<Long, Item> items = new HashMap<>();
  private Long idsCounter = 0L;

  @Override
  public Item createItem(Item item) {
    item.setId(++idsCounter);
    items.put(item.getId(), item);
    log.info("Создан item: {}", item);

    return items.get(item.getId());
  }

  @Override
  public Item updateItem(Item item) {
    items.put(item.getId(), item);
    log.info("Обновлен item: {}", item);

    return items.get(item.getId());
  }

  @Override
  public Item getItem(Long itemId) {
    return items.get(itemId);
  }

  @Override
  public List<Item> getItems(Long ownerId) {
    return items.values().stream()
        .filter(s -> s.getOwnerId().equals(ownerId))
        .collect(Collectors.toList());
  }

  @Override
  public List<Item> searchItem(String searchCriteria) {
    return items.values().stream()
        .filter(s -> s.getName().toLowerCase(Locale.ROOT).contains(searchCriteria)
            || s.getDescription().toLowerCase(Locale.ROOT).contains(searchCriteria))
        .filter(Item::getIsAvailable)
        .collect(Collectors.toList());
  }
}
