package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items;
    private static final AtomicLong id = new AtomicLong(0);

    @Override
    public Item createItem(Item item) {
        item.setId(id.incrementAndGet());
        items.put(item.getId(), item);
        log.info("Добавлен новый предмет с id={}", item.getId());

        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.remove(item.getId());
        items.put(item.getId(), item);
        log.info("Обновлен предмет с id={}", item.getId());

        return items.get(item.getId());
    }

    @Override
    public Item getItemById(Long itemId) {
        log.info("Получен предмет с id={}", itemId);

        return items.get(itemId);
    }

    @Override
    public List<Item> getAllItemsOfUser(Long userId) {
        log.info("Получен список всех предметов пользователя с id={}", userId);

        return items.values()
                .stream()
                .filter(s -> s.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItem(String searchQuery) {
        log.info("Получен список предметов, в названии или в описании которых содержится '{}'", searchQuery);

        return items.values()
                .stream()
                .filter(s -> s.getName().toLowerCase(Locale.ROOT).contains(searchQuery)
                        || s.getDescription().toLowerCase(Locale.ROOT).contains(searchQuery))
                .filter(item -> item.getIsAvailable())
                .collect(Collectors.toList());
    }

}
