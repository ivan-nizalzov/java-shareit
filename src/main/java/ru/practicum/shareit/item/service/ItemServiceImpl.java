package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemWithBookingResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.item.validator.ItemValidator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        ItemValidator.validateItem(itemDto);
        checkUserInDb(userId);

        Item item = itemMapper.convertDtoToItem(itemDto);
        User owner = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id=" + userId + " не найден в БД"));
        item.setOwner(owner);
        item.setRequest(null);
        //Item createdItem = itemDAO.createItem(item);
        Item createdItem = itemRepository.save(item);

        return itemMapper.convertItemToDto(createdItem);
    }

    @Override
    public ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto) {
        //Item oldItem = itemDAO.getItemById(itemId);
        Item oldItem = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Предмет с id=" + itemId + " не найден в БД"));

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

        //Item item = itemDAO.updateItem(editedItem);
        Item item = itemRepository.save(editedItem);

        return itemMapper.convertItemToDto(item);
    }

    @Override
    public ItemWithBookingResponseDto getItemById(Long itemId, Long userId) {

        /*if (itemDAO.getItemById(itemId) == null) {
            throw new NotFoundException("Предмет с id=" + itemId + " не найден");
        }*/

        if (itemRepository.findById(itemId) == null) {
            throw new NotFoundException("Предмет с id=" + itemId + " не найден");
        }

        //return itemMapper.convertItemToDto(itemDAO.getItemById(itemId));
        //return itemMapper.convertItemToDto(itemRepository.findById(itemId).get()); //Wrapped in Optional<Item>

        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Вещь с id=" + itemId + " не найдена в БД"));

        Booking lastBooking = bookingRepository.findByItemId_AndEndDateTimeBeforeOrder_ByEndDateTime_Desc(itemId,
                LocalDateTime.now());

        Booking nextBooking = bookingRepository.findByItemId_AndStartDateTimeAfterOrder_ByEndDateTime_Asc(itemId,
                LocalDateTime.now());

        List<CommentDto> comments = commentRepository.findAllByItemId(itemId)
                .stream()
                .map(itemMapper::convertDtoToComment)
                .collect(Collectors.toList());

        ItemWithBookingResponseDto itemWithBookingResponseDto = itemMapper.convertToItemWithResponseDto(item);
        itemWithBookingResponseDto.setComments(comments);

        if (userId.equals(item.getOwner().getId())) {
            itemWithBookingResponseDto.setLastBooking(bookingMapper.convertToBookingShortInfo(lastBooking));
            itemWithBookingResponseDto.setNextBooking(bookingMapper.convertToBookingShortInfo(nextBooking));
        }

        return itemWithBookingResponseDto;
    }

    @Override
    public List<ItemWithBookingResponseDto> getAllItemsOfUser(Long userId) {
        /*return itemDAO.getAllItemsOfUser(userId)
                .stream()
                .map(itemMapper::convertItemToDto)
                .collect(Collectors.toList());*/

        return itemRepository.findAllByOwnerId(userId).stream()
                .map(s -> {
                    Booking lastBooking = bookingRepository.findByItemId_AndEndDateTimeBeforeOrder_ByEndDateTime_Desc(
                            s.getId(), LocalDateTime.now()
                    );
                    Booking nextBooking = bookingRepository.findByItemId_AndStartDateTimeAfterOrder_ByEndDateTime_Asc(
                            s.getId(), LocalDateTime.now()
                    );

                    ItemWithBookingResponseDto itemWithBookingResponseDto = itemMapper.convertToItemWithResponseDto(s);
                    itemWithBookingResponseDto.setLastBooking(bookingMapper.convertToBookingShortInfo(lastBooking));
                    itemWithBookingResponseDto.setNextBooking(bookingMapper.convertToBookingShortInfo(nextBooking));

                    return itemWithBookingResponseDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(String searchQuery) {
        if (searchQuery.isBlank()) {
            return Collections.emptyList();
        }

        /*return itemDAO.searchItem(searchQuery)
                .stream()
                .map(itemMapper::convertItemToDto)
                .collect(Collectors.toList());*/

        return itemRepository.findAllByNameOrDescription(searchQuery)
                .stream()
                .map(itemMapper::convertItemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id=" + userId + " не найден в БД"));

        if(!bookingRepository.existsByBookerId_AndItemIdAndEndDateTimeBefore(userId, itemId, LocalDateTime.now())) {
            throw new ValidationException("Пользователь с id=" + userId + " не бронировал вещь с id=" + itemId);
        }

        Comment comment = commentRepository.save(itemMapper.convertDtoToComment(commentDto, userId, itemId));
    }

    private void checkUserInDb(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден в БД");
        }
    }

}
