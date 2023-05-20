package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        LocalDateTime dateTime = LocalDateTime.now();
        ItemRequest newItemRequest = itemRequestMapper.toItemRequest(
                itemRequestDto, getUserById(userId), dateTime);
        ItemRequest itemRequest = itemRequestRepository.save(newItemRequest);

        log.info("Created new itemRequest with id={}.", itemRequest.getId());

        return itemRequestMapper.toItemRequestDto(itemRequest);
    }

    public List<ItemRequestDto> findAllRequestsOfUser(Long userId) {
        checkUserInDb(userId);

        List<ItemRequest> itemRequestList = itemRequestRepository.findByRequestorIdOrderByCreatedAsc(userId);
        log.info("Found all item requests ({}) of user with id={}.", itemRequestList.size(), userId);

        return itemRequestList.stream()
                .map(itemRequestMapper::toItemRequestDto)
                .peek(itemRequestDto -> itemRequestDto.setItems(getItemDtoListByRequestId(itemRequestDto.getId())))
                .collect(Collectors.toList());
    }


    //Получить список всех запросов кроме своих
    public List<ItemRequestDto> findAllRequestsExceptYours(Long userId, Integer from, Integer size) {
        checkUserInDb(userId);

        PageRequest page = PageRequest.of(from, size);
        List<ItemRequest> itemRequestsList = itemRequestRepository.findByRequestorIdNotOrderByCreatedAsc(userId, page);
        log.info("Found all item requests ({}) except made by user with id={}.", itemRequestsList.size(), userId);

        return itemRequestsList.stream()
                .map(itemRequestMapper::toItemRequestDto)
                .peek(itemRequestDto -> itemRequestDto.setItems(getItemDtoListByRequestId(itemRequestDto.getId())))
                .collect(Collectors.toList());
    }

    public ItemRequestDto findById(Long userId, Long requestId) {
        checkUserInDb(userId);

        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("ItemRequest with id=" + requestId + " not found."));

        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(getItemDtoListByRequestId(requestId));

        log.info("Found item request with id={}.", requestId);

        return itemRequestDto;
    }

    private void checkUserInDb(Long userId) {
        if (userId == null) {
            throw new UnsupportedStatus("UserId is null");
        }
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found."));
    }

    private List<ItemDto> getItemDtoListByRequestId(Long requestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Item request with id=" + requestId + " not found."));

        return itemRepository.findByRequest(itemRequest).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private User getUserById(Long userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found."));
    }

}
