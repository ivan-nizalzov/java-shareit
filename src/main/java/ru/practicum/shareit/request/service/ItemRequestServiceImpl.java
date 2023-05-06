package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final UserMapper userMapper;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId) {
        ItemRequest itemRequest = ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requester(userMapper.toUser(userService.findUserById(userId)))
                .created(LocalDateTime.now())
                .build();

        return itemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public ItemRequestDto findById(Long userId, Long requestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Request with id = %d not found.", requestId)));
        itemRequest.setItems(itemRepository.findAllByItemRequest(itemRequest));
        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setRequester(userService.findUserById(userId));

        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> findAllRequests(Long userId, Integer from, Integer size) {
        userMapper.toUser(userService.findUserById(userId));
        checkPageRequest(from, size);
        Pageable page = PageRequest.of(from / size, size, Sort.by("created"));

        return itemRequestRepository.findAllByRequesterIdIsNot(userId, page).stream()
                .peek(itemRequest -> itemRequest.setItems(itemRepository.findAllByItemRequest(itemRequest)))
                .map(itemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());

    }

    @Override
    public List<ItemRequestDto> findAllUserRequests(Long userId) {
        userService.findUserById(userId);

        return itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId).stream()
                .peek(i -> i.setItems(itemRepository.findAllByItemRequest(i)))
                .map(itemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    private void checkPageRequest(Integer from, Integer size) {
        if (from < 0) {
            throw new BadRequestException("Bad request: PageRequest 'from' cannot be less than one");
        }
    }

}
