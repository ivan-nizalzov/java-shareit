package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import static ru.practicum.shareit.user.util.UserHeader.USER_HEADER;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_HEADER) @NotNull Long userId,
                                         @Valid @RequestBody ItemRequestDto itemRequestDto) {

        log.info("Creating item request {}", itemRequestDto);
        return itemRequestClient.create(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllRequestsOfUser(@RequestHeader(USER_HEADER) @NotNull Long userId) {
        log.info("Finding all requests of user with id={}", userId);
        return itemRequestClient.findAllRequestsOfUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllRequestsExceptYours(
            @RequestHeader(USER_HEADER) @NotNull Long userId,
            @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
            @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {

        log.info("Finding all request except userId={}", userId);
        return itemRequestClient.findAllRequestsExceptYours(userId, from, size);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> findById(@RequestHeader(USER_HEADER) @NotNull Long userId,
                                   @PathVariable Long requestId) {

        log.info("Finding item request with id={}, userId={}", requestId, userId);
        return itemRequestClient.findById(userId, requestId);
    }

}