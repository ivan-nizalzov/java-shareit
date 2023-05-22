package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> findAllRequestsOfUser(Long userId);

    List<ItemRequestDto> findAllRequestsExceptYours(Long userId, Integer from, Integer size);

    ItemRequestDto findById(Long userId, Long requestId);
}
