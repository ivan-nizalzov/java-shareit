package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<ItemDto> create(
            @RequestHeader(USER_HEADER) Long userId,
            @Valid @RequestBody ItemDto itemDto) {
        log.debug("POST /items : create new item");
        return ResponseEntity.ok(itemService.create(userId, itemDto));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> findById(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable Long itemId) {
        log.debug("GET /items/{itemId} : get item by id");
        return ResponseEntity.ok(itemService.findItemById(itemId, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> findAll(
            @RequestHeader(USER_HEADER) Long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.debug("GET /items : get all items");
        return ResponseEntity.ok(itemService.findAllItemsOfUser(userId, from, size));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto) {
        log.debug("PATCH /items/{itemId} : update item");
        return ResponseEntity.ok(itemService.update(itemDto, itemId, userId));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable Long itemId) {
        log.debug("DELETE /items/{itemId} : delete item");
        itemService.deleteById(itemId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<ItemDto>> search
            (@RequestParam String text,
             @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
             @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.debug("GET /items/search : search item");
        return ResponseEntity.ok(itemService.search(text, from, size));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> createComment(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody CommentDto commentDto) {
        log.debug("POST /items/{itemId}/comment : add comment to item");
        return ResponseEntity.ok(itemService.addComment(itemId, userId, commentDto));
    }
}