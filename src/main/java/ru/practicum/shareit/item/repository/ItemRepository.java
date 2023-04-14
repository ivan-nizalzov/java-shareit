package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(Long ownerId);

    @Query(value = "SELECT i " +
            "FROM items i " +
            "WHERE UPPER(i.name) like CONCAT('%', UPPER(:searchQuery), '%') " +
            "OR UPPER(i.description) like CONCAT('%', UPPER(:searchQuery), '%') " +
            "AND is_available = true ")
    List<Item> findAllByNameOrDescription(@Param("searchQuery") String searchQuery);

}
