package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ForbiddenAccessException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserServiceImpl userServiceImpl;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        userServiceImpl.findUserById(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwnerId(userId);
        log.info("Created new Item with id={}.", item.getId());

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto findItemById(Long itemId, Long userId) {
        ItemDto result;
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException(String.format("Item с id = %d не найден.", itemId)));
        result = ItemMapper.toItemDto(item);

        if (Objects.equals(item.getOwnerId(), userId)) {
            updateBookings(result);
        }

        List<Comment> comments = commentRepository.findAllByItemId(result.getId());
        result.setComments(CommentMapper.toDtoList(comments));
        log.info("Found Item with id={}.", itemId);

        return result;
    }

    @Transactional
    @Override
    public List<ItemDto> findAllItemsOfUser(Long userId) {
        List<ItemDto> item = itemRepository.findAllByOwnerId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        log.info("Found all items of User with id={}.", userId);

        return item.stream()
                .map(this::updateBookings)
                .peek((i) -> CommentMapper.toDtoList(commentRepository.findAllByItemId(i.getId())))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException(String.format("Item with id = %d not found.", itemId)));
        userServiceImpl.findUserById(userId);

        if (!item.getOwnerId().equals(userId)) {
            throw new ForbiddenAccessException(String.format("User with id = %d is not the owner, " +
                    "update is not available.", userId));
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        log.info("Updated Item with id={}.", itemId);

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateBookings(ItemDto itemDto) {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findAllBookingsItem(itemDto.getId());

        Booking lastBooking = bookings.stream()
                .filter(obj -> !(obj.getStatus().equals(BookingStatus.REJECTED)))
                .filter(obj -> obj.getStart().isBefore(now))
                .min((obj1, obj2) -> obj2.getStart().compareTo(obj1.getStart())).orElse(null);

        Booking nextBooking = bookings.stream()
                .filter(obj -> !(obj.getStatus().equals(BookingStatus.REJECTED)))
                .filter(obj -> obj.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart)).orElse(null);

        if (lastBooking != null) {
            itemDto.setLastBooking(BookingMapper.toItemBookingDto(lastBooking));
        }
        if (nextBooking != null) {
            itemDto.setNextBooking(BookingMapper.toItemBookingDto(nextBooking));
        }

        log.info("Updated bookings of Item with id={}..", itemDto.getId());

        return itemDto;
    }

    @Transactional
    @Override
    public void deleteById(Long itemId) {
        itemRepository.deleteById(itemId);
        log.info("Deleted Item with id={}.", itemId);
    }

    @Transactional
    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        log.info("Found all items via search request='{}'.", text);

        return itemRepository.searchAvailableItems(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public Long findOwnerId(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with id = %d not found.", itemId)))
                .getOwnerId();
    }

    @Transactional
    @Override
    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException(String.format("Item with id = %d not found.", itemId)));
        User user = UserMapper.toUser(userServiceImpl.findUserById(userId));

        List<Booking> bookings = bookingRepository.findAllByItemIdAndBookerIdAndStatusIsAndEndIsBefore(
                itemId, userId, BookingStatus.APPROVED, LocalDateTime.now());

        if (!bookings.isEmpty() && bookings.get(0).getStart().isBefore(LocalDateTime.now())) {
            Comment comment = CommentMapper.toComment(commentDto);
            comment.setItem(item);
            comment.setAuthor(user);
            comment.setCreated(LocalDateTime.now());

            log.info("Added comment to Item with id={}.", itemId);

            return CommentMapper.toDto(commentRepository.save(comment));
        } else {
            throw new NotAvailableException(String.format("Booking for User with id = %d and Item with id = %d not found.",
                    userId, itemId));
        }
    }

}