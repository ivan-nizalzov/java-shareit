package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final String USER_HEADER = "X-Sharer-User-Id";
    @PostMapping
    public ResponseEntity<ItemRequestDto> create(@RequestHeader(USER_HEADER) Long userId,
                                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.debug("POST /requests : create new request");
        return ResponseEntity.ok(itemRequestService.create(itemRequestDto, userId));
    }

    @GetMapping("{requestId}")
    public ResponseEntity<ItemRequestDto> findRequestById(@RequestHeader(USER_HEADER) Long userId,
                                                          @PathVariable Long requestId) {
        log.debug("GET /requests/{requestId} : get request by id");
        return ResponseEntity.ok(itemRequestService.findById(userId, requestId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> findAllRequests(@RequestHeader(USER_HEADER) Long userId,
                                                                @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                                @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.debug("GET /requests/all : get all requests");
        return ResponseEntity.ok(itemRequestService.findAllRequests(userId, from, size));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getAllUserRequests(@RequestHeader(USER_HEADER) Long userId) {
        log.debug("GET GET /requests : get all requests of user");
        return ResponseEntity.ok(itemRequestService.findAllUserRequests(userId));
    }

}
