package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.item.validator.ItemValidator;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        ItemValidator.validateItem(itemDto);
        checkUserInDb(userId);

        Item item = itemMapper.convertDtoToItem(itemDto);
        User owner = userMapper.convertDtoToUser(userService.getUserById(userId));
        item.setOwner(owner);
        item.setRequest(null);
        Item createdItem = itemRepository.createItem(item);

        return itemMapper.convertItemToDto(createdItem);
    }

    @Override
    public ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto) {
        Item oldItem = itemRepository.getItemById(itemId);

        if (!oldItem.getOwner().getId().equals(userId)) {
            throw new ForbiddenAccessException("Предмет с id=" + itemId + " не может быть отредактирован пользователем" +
                    " с id=" + userId);
        }

        Item editedItem = Item.builder()
                .id(itemId)
                .name(itemDto.getName() != null ? itemDto.getName() : oldItem.getName())
                .description(itemDto.getDescription() != null ? itemDto.getDescription() : oldItem.getDescription())
                .isAvailable(itemDto.getIsAvailable() != null ? itemDto.getIsAvailable() : oldItem.getIsAvailable())
                .owner(oldItem.getOwner())
                .request(oldItem.getRequest())
                .build();

        Item item = itemRepository.updateItem(editedItem);

        return itemMapper.convertItemToDto(item);
    }

    @Override
    public ItemDto getItemById(Long itemId) {

        if (itemRepository.getItemById(itemId) == null) {
            throw new NotFoundException("Предмет с id=" + itemId + " не найден");
        }

        return itemMapper.convertItemToDto(itemRepository.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getAllItemsOfUser(Long userId) {
        return itemRepository.getAllItemsOfUser(userId)
                .stream()
                .map(itemMapper::convertItemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(String searchQuery) {
        if (searchQuery.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.searchItem(searchQuery)
                .stream()
                .map(itemMapper::convertItemToDto)
                .collect(Collectors.toList());
    }

    private void checkUserInDb(Long userId) {
        if (userService.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
    }

}
