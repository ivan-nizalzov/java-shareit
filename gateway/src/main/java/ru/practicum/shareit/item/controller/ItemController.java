package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import static ru.practicum.shareit.user.util.UserHeader.USER_HEADER;

@Slf4j
@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(USER_HEADER) Long userId,
            @Valid @RequestBody ItemDto itemDto) {

        return ResponseEntity.ok(itemClient.create(userId, itemDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable Long id,
            @RequestBody ItemDto itemDto) {

        return ResponseEntity.ok(itemClient.update(userId, id, itemDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable Long id) {

        return ResponseEntity.ok(itemClient.findById(userId, id));
    }

    @GetMapping
    public ResponseEntity<Object> findAllItemsOfUser(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
            @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {

        return ResponseEntity.ok(itemClient.findAllItemsOfUser(userId, from, size));
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestParam(name = "text") String text,
            @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
            @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {

        return ResponseEntity.ok(itemClient.search(userId, text, from, size));
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable Long id,
            @Valid @RequestBody CommentDto comment) {

        return ResponseEntity.ok(itemClient.addComment(userId, id, comment));
    }
}
