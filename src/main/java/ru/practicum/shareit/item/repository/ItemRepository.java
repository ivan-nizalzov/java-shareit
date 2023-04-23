package ru.practicum.shareit.item.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

  List<Item> findAllByOwnerId(Long ownerId);

  @Query(value = "SELECT i "
      + "FROM items i "
      + "WHERE UPPER(i.name) Like CONCAT('%',UPPER(:searchCriteria),'%') "
      + "OR UPPER(i.description) Like CONCAT('%',UPPER(:searchCriteria),'%') "
      + "AND is_available = true")
  List<Item> findAllByNameOrDescription(@Param("searchCriteria") String searchCriteria);
}
