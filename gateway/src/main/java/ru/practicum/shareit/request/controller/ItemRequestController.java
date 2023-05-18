package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import static ru.practicum.shareit.user.util.UserHeader.USER_HEADER;

@Validated
@RequestMapping(path = "/requests")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_HEADER) Long userId,
                                         @Valid @RequestBody ItemRequestDto itemRequestDto) {

        return ResponseEntity.ok(itemRequestClient.create(userId, itemRequestDto));
    }

    @GetMapping
    public ResponseEntity<Object> findAllRequestsOfUser(@RequestHeader(USER_HEADER) Long userId) {
        return ResponseEntity.ok(itemRequestClient.findAllRequestsOfUser(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllRequestsExceptYours(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
            @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {

        return ResponseEntity.ok(itemRequestClient.findAllRequestsExceptYours(userId, from, size));
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> findById(@RequestHeader(USER_HEADER) Long userId,
                                   @PathVariable Long requestId) {

        return ResponseEntity.ok(itemRequestClient.findById(userId, requestId));
    }

}