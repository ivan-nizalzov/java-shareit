package ru.practicum.shareit.item.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemWithBookingInfoDto;
import ru.practicum.shareit.item.exception.NoPermitsException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImpl implements ItemService {

  private final ItemRepository storage;
  private final UserRepository userRepository;
  private final BookingRepository bookingRepository;
  private final CommentRepository commentRepository;

  @Override
  public ItemDto createItem(long userId, ItemDto itemDto) {
    userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
    checkFieldsFilled(itemDto);

    var item = storage.save(ItemMapper.toItem(itemDto, userId, null));
    return ItemMapper.toItemDto(item);
  }

  @Override
  public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
    var itemPreviousVersion = storage.findById(itemId)
        .orElseThrow(() -> new NoSuchElementException("Previous version of item not found"));

    if (itemPreviousVersion.getOwnerId() != userId) {
      throw new NoPermitsException("Пользователь с id: " + userId + " не имеет прав на редактирование данной вещи");
    }

    var updatedItem = Item.builder()
        .id(itemId)
        .name(itemDto.getName() != null ? itemDto.getName() : itemPreviousVersion.getName())
        .description(itemDto.getDescription() != null ? itemDto.getDescription() : itemPreviousVersion.getDescription())
        .isAvailable(itemDto.getIsAvailable() != null ? itemDto.getIsAvailable() : itemPreviousVersion.getIsAvailable())
        .ownerId(itemPreviousVersion.getOwnerId())
        .request(itemPreviousVersion.getRequest())
        .build();

    var item = storage.save(updatedItem);
    return ItemMapper.toItemDto(item);
  }

  @Override
  public ItemWithBookingInfoDto getItem(long userId, Long itemId) {
    var item = storage.findById(itemId).orElseThrow(NoSuchElementException::new);
    var lastBooking = bookingRepository.findByItemIdAndEndDateTimeBeforeOrderByEndDateTimeDesc(itemId,
        LocalDateTime.now());
    var nextBooking = bookingRepository.findByItemIdAndStartDateTimeAfterOrderByEndDateTimeAsc(itemId,
        LocalDateTime.now());
    var comments = commentRepository.findAllByItemId(itemId).stream()
        .map(CommentMapper::toCommentDto)
        .collect(Collectors.toList());

    var itemWithBookingInfoDto = ItemMapper.toItemDtoWithBookingInfoDto(item);
    itemWithBookingInfoDto.setComments(comments);

    if (userId == item.getOwnerId()) {
      itemWithBookingInfoDto.setLastBooking(BookingMapper.toBookingShortInfo(lastBooking));
      itemWithBookingInfoDto.setNextBooking(BookingMapper.toBookingShortInfo(nextBooking));
    }

    return itemWithBookingInfoDto;
  }

  @Override
  public List<ItemWithBookingInfoDto> getItems(Long ownerId) {
    return storage.findAllByOwnerId(ownerId)
        .stream()
        .map(s -> {
          var lastBooking = bookingRepository.findByItemIdAndEndDateTimeBeforeOrderByEndDateTimeDesc(s.getId(),
              LocalDateTime.now());
          var nextBooking = bookingRepository.findByItemIdAndStartDateTimeAfterOrderByEndDateTimeAsc(s.getId(),
              LocalDateTime.now());

          var itemWithBookingInfoDto = ItemMapper.toItemDtoWithBookingInfoDto(s);
          itemWithBookingInfoDto.setLastBooking(BookingMapper.toBookingShortInfo(lastBooking));
          itemWithBookingInfoDto.setNextBooking(BookingMapper.toBookingShortInfo(nextBooking));

          return itemWithBookingInfoDto;
        })
        .collect(Collectors.toList());
  }

  @Override
  public List<ItemDto> searchItem(String searchCriteria) {
    if (StringUtils.isBlank(searchCriteria)) {
      return Collections.emptyList();
    }

    return storage.findAllByNameOrDescription(searchCriteria)
        .stream()
        .map(ItemMapper::toItemDto)
        .collect(Collectors.toList());
  }

  @Override
  public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
    var user = userRepository.findById(userId).orElseThrow();

    if (!bookingRepository.existsByBookerIdAndItemIdAndEndDateTimeBefore(userId, itemId, LocalDateTime.now())) {
      throw new IllegalStateException("Current user didn't book this item");
    }

    var comment = commentRepository.save(CommentMapper.toComment(commentDto, userId, itemId));
    comment.setAuthor(user);

    return CommentMapper.toCommentDto(comment);
  }

  private void checkFieldsFilled(ItemDto itemDto) {
    var isNameFilledCorrectly = StringUtils.isNoneBlank(itemDto.getName());
    var isDescriptionFilledCorrectly = StringUtils.isNoneBlank(itemDto.getDescription());
    var isAvailableFilledCorrectly = itemDto.getIsAvailable() != null;

    if (!isNameFilledCorrectly || !isDescriptionFilledCorrectly || !isAvailableFilledCorrectly) {
      throw new IllegalArgumentException("Некорректно заполнены поля объекта item");
    }
  }
}
