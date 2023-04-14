package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.Locale;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestHeader(USER_HEADER) Long userId,
                              @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok().body(itemService.createItem(itemDto, userId));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader(USER_HEADER) Long userId,
                              @PathVariable("itemId") Long itemId,
                              @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok().body(itemService.updateItem(itemId, userId, itemDto));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemWithBookingResponseDto> getItemById(@RequestHeader(USER_HEADER) long userId,
                                                                  @PathVariable("itemId") Long itemId) {
        return ResponseEntity.ok().body(itemService.getItemById(itemId, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemWithBookingResponseDto>> getAllItemsOfUser(@RequestHeader(USER_HEADER) long userId) {
        return ResponseEntity.ok().body(itemService.getAllItemsOfUser(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItem(@RequestParam("text") String searchQuery) {
        return ResponseEntity.ok().body(itemService.searchItem(searchQuery.toLowerCase(Locale.ROOT)));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(USER_HEADER) long userId,
                                 @PathVariable long itemId,
                                 @Valid @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }

}
