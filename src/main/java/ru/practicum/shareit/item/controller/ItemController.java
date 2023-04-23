package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Slf4j
public class ItemController {
    private final ItemServiceImpl itemServiceImpl;
    private final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<ItemDto> create(@RequestHeader(USER_HEADER) Long userId,
                                          @Valid @RequestBody ItemDto itemDto) {
        log.debug("POST-запрос на создание новой вещи.");

        return ResponseEntity.ok(itemServiceImpl.create(userId, itemDto));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> findById(@RequestHeader(USER_HEADER) Long userId, @PathVariable Long itemId) {
        log.debug("GET-запрос на получение вещи по идентификатору.");

        return ResponseEntity.ok(itemServiceImpl.findItemById(itemId, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> findAll(@RequestHeader(USER_HEADER) Long userId) {
        log.debug("GET-запрос на получение всех вещей пользователя по идентификатору.");

        return ResponseEntity.ok(itemServiceImpl.findAllItemsOfUser(userId));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(@RequestHeader(USER_HEADER) Long userId, @PathVariable long itemId,
                          @RequestBody ItemDto itemDto) {
        log.debug("PATCH-запрос на обновление вещи.");

        return ResponseEntity.ok(itemServiceImpl.update(itemDto, itemId, userId));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId) {
        log.debug("DELETE-запрос на удаление вещи.");
        itemServiceImpl.deleteById(itemId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<ItemDto>> search(@RequestParam String text) {
        log.debug("GET-запрос на поиск вещей.", text);

        return ResponseEntity.ok(itemServiceImpl.search(text));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> createComment(@RequestHeader(USER_HEADER) Long userId,
                                    @PathVariable Long itemId,
                                    @Valid @RequestBody CommentDto commentDto) {
        log.debug("POST-запрос на добавление отзыва.");

        return ResponseEntity.ok(itemServiceImpl.addComment(itemId, userId, commentDto));
    }
}