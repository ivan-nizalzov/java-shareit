package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
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
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.user.util.UserHeader.USER_HEADER;

@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> create(
            @RequestHeader(USER_HEADER) Long userId,
            @Valid @RequestBody ItemDto itemDto) {

        return ResponseEntity.ok(itemService.create(userId, itemDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDto> update(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable Long id,
            @RequestBody ItemDto itemDto) {

        return ResponseEntity.ok(itemService.update(userId, id, itemDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> findById(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable Long id) {

        return ResponseEntity.ok(itemService.findById(userId, id));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> findAllItemsOfUser(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
            @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {

        return ResponseEntity.ok(itemService.findAllItemsOfUser(userId, from, size));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(
            @RequestParam(name = "text") String text,
            @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
            @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {

        return ResponseEntity.ok(itemService.search(text, from, size));
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<CommentDto> addComment(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable Long id,
            @Valid @RequestBody CommentDto comment) {

        return ResponseEntity.ok(itemService.addComment(userId, id, comment));
    }
}
