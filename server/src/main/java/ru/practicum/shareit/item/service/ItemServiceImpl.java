package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.ForbiddenAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final BookingMapper bookingMapper;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Transactional
    public ItemDto create(Long userId, ItemDto itemDto) {
        Item newItem = itemMapper.toItem(itemDto, getUserById(userId));
        newItem.setRequest(getItemRequest(itemDto.getRequestId()));
        Item item = itemRepository.save(newItem);
        log.info("Created new item with id={}.", item.getId());

        return itemMapper.toItemDto(item);
    }

    @Transactional
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Item updateItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id=" + itemId + " not found."));

        checkIsOwner(userId, updateItem);

        ofNullable(itemDto.getName()).ifPresent(updateItem::setName);
        ofNullable(itemDto.getDescription()).ifPresent(updateItem::setDescription);
        ofNullable(itemDto.getAvailable()).ifPresent(updateItem::setAvailable);

        log.info("Updated ite, with id={}.", itemId);

        return itemMapper.toItemDto(itemRepository.save(updateItem));
    }

    @Override
    public ItemDto findById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id=" + itemId + " not found."));

        log.info("Found item with id={}.", itemId);

        return setBookingAndCommentInfo(item, userId);
    }

    @Override
    public List<ItemDto> findAllItemsOfUser(Long userId, Integer from, Integer size) {
        int start = from / size;
        PageRequest page = PageRequest.of(start, size);
        List<Item> itemList = itemRepository.findByOwnerId(userId, page);

        log.info("Found all items ({}) of user with id={}.", itemList.size(), userId);

        return itemList.stream()
                .map(item -> setBookingAndCommentInfo(item, userId))
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String searchQuery, Integer from, Integer size) {
        int start = from / size;
        PageRequest page = PageRequest.of(start, size);

        if (searchQuery.isEmpty()) {
            log.info("Search query is empty.");
            return new ArrayList<>();
        } else {
            List<Item> itemList = itemRepository.search(searchQuery, page);
            log.info("Search query: found " + itemList.size() + " items.");

            return itemList.stream()
                    .map(itemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {
        Comment comment = commentMapper.toComment(commentDto);
        Item item = itemRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Item with id=" + userId + " not found."));
        LocalDateTime dateTime = LocalDateTime.now();
        List<Booking> bookingList = bookingRepository.findBookingsByItem(item, BookingStatus.APPROVED, itemId, dateTime);

        if (!bookingList.isEmpty()) {
            comment.setItem(item);
            comment.setAuthor(bookingList.get(0).getBooker());
            comment.setCreated(dateTime);

            log.info("Added comments of user with id={} to item with id={}.", userId, itemId);

            return commentMapper.toCommentDto(commentRepository.save(comment));
        } else {
            throw new BadRequestException("Error: Cannot add a comment because of empty booking list.");
        }
    }

    private void checkIsOwner(Long ownerId, Item item) {
        if (!Objects.equals(item.getOwner().getId(), ownerId)) {
            throw new ForbiddenAccessException("User with id=" + ownerId + " is not the owner.");
        }
    }

    private ItemDto setBookingAndCommentInfo(Item item, Long userId) {
        LocalDateTime dateTime = LocalDateTime.now();
        ItemDto itemDto = itemMapper.toItemDto(item);

        List<CommentDto> commentDtoList = commentRepository.findByItem(item).stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());

        itemDto.setComments(commentDtoList);

        if (item.getOwner().getId().equals(userId)) {

            List<Booking> lastBooking = bookingRepository
                    .findDByItemAndStatusAndStartBeforeOrderByEndDesc(item, BookingStatus.APPROVED, dateTime);

            itemDto.setLastBooking(lastBooking.isEmpty() ? null : bookingMapper.toBookingInfoDto(lastBooking.get(0)));

            List<Booking> nextBooking = bookingRepository
                    .findDByItemAndStatusAndStartAfterOrderByStartAsc(item, BookingStatus.APPROVED, dateTime);

            itemDto.setNextBooking(nextBooking.isEmpty() ? null : bookingMapper.toBookingInfoDto(nextBooking.get(0)));
        }

        return itemDto;
    }

    private ItemRequest getItemRequest(Long itemRequestId) {
        if (itemRequestId == null) {
            return null;
        }
        return itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException("Item request with id=" + itemRequestId + " not found."));
    }

    private User getUserById(Long userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found."));
    }

}
