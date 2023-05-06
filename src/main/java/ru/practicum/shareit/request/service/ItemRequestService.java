package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId);
    ItemRequestDto findById(Long userId, Long requestId);
    List<ItemRequestDto> findAllRequests(Long userId, Integer from, Integer size);
    List<ItemRequestDto> findAllUserRequests(Long userId);

}
