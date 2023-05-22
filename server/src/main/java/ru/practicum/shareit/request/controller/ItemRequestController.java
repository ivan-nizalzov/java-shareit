package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
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
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

import static ru.practicum.shareit.user.util.UserHeader.USER_HEADER;

@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService requestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> create(@RequestHeader(USER_HEADER) Long userId,
                                                @RequestBody ItemRequestDto itemRequestDto) {

        return ResponseEntity.ok(requestService.create(userId, itemRequestDto));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> findAllRequestsOfUser(@RequestHeader(USER_HEADER) Long userId) {
        return ResponseEntity.ok(requestService.findAllRequestsOfUser(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> findAllRequestsExceptYours(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size) {

        return ResponseEntity.ok(requestService.findAllRequestsExceptYours(userId, from, size));
    }

    @GetMapping("{requestId}")
    public ResponseEntity<ItemRequestDto> findById(@RequestHeader(USER_HEADER) Long userId,
                                   @PathVariable Long requestId) {

        return ResponseEntity.ok(requestService.findById(userId, requestId));
    }

}