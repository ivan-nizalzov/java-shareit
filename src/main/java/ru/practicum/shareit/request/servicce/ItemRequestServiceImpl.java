package ru.practicum.shareit.request.servicce;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final RequestRepository repository;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        LocalDateTime dateTime = LocalDateTime.now();
        ItemRequest newItemRequest = itemRequestMapper.toItemRequest(
                itemRequestDto, userService.findById(userId).getId(), dateTime);
        ItemRequest itemRequest = repository.save(newItemRequest);

        log.info("Created new itemRequest with id={}.", itemRequest.getId());

        return itemRequestMapper.toItemRequestDto(itemRequest);
    }

    public List<ItemRequestDto> findAllRequestsOfUser(Long userId) {
        checkUserInDb(userId);

        List<ItemRequest> itemRequestList = repository.findByRequestorIdOrderByCreatedAsc(userId);
        log.info("Found all item requests ({}) of user with id={}.", itemRequestList.size(), userId);

        return itemRequestList.stream()
                .map(itemRequestMapper::toItemRequestDto)
                .peek(itemRequestDto -> itemRequestDto.setItems(itemRepository.findByRequestId(itemRequestDto.getId())))
                .collect(Collectors.toList());
    }


    //Получить список всех запросов кроме своих
    public List<ItemRequestDto> findAllRequestsExceptYours(Long userId, Integer from, Integer size) {
        checkUserInDb(userId);

        PageRequest page = PageRequest.of(from, size);
        List<ItemRequest> itemRequestsList = repository.findByRequestorIdNotOrderByCreatedAsc(userId, page);
        log.info("Found all item requests ({}) except made by user with id={}.", itemRequestsList.size(), userId);

        return itemRequestsList.stream()
                .map(itemRequestMapper::toItemRequestDto)
                .peek(itemRequestDto -> itemRequestDto.setItems(itemRepository.findByRequestId(itemRequestDto.getId())))
                .collect(Collectors.toList());
    }

    public ItemRequestDto findById(Long userId, Long requestId) {
        checkUserInDb(userId);

        ItemRequest itemRequest = repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("ItemRequest with id=" + requestId + " not found."));

        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(itemRepository.findByRequestId(requestId));

        log.info("Found item request with id={}.", requestId);

        return itemRequestDto;
    }

    private void checkUserInDb(Long userId) {
        userService.findById(userId);
    }

}
