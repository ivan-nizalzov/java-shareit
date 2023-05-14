package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exception.ForbiddenAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class ItemServiceImplTest {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private ItemDto itemDto;
    private UserDto secondUserDto;
    private ItemDto updateItemDto;
    private UserDto testUser;
    private UserDto secondUserFromDB;

    @BeforeEach
    public void setUp() {
        itemDto = ItemDto.builder()
                .name("Дрель")
                .description("Простая дрель")
                .available(true)
                .build();

        updateItemDto = ItemDto.builder()
                .name("Дрель+")
                .description("Аккумуляторная дрель")
                .available(true)
                .build();

        UserDto userDto = UserDto.builder()
                .email("test@test.com")
                .name("testName")
                .build();

        secondUserDto = UserDto.builder()
                .email("second@test.com")
                .name("secondName")
                .build();

        testUser = userService.create(userDto);
        secondUserFromDB = userService.create(secondUserDto);
    }

    @Test
    void createItemTest() {
        ItemDto itemDtoFromDB = itemService.create(testUser.getId(), itemDto);

        checkItemsAreTheSame(itemDtoFromDB, itemDto);
    }


    @Test
    void updateItemTest() {
        ItemDto itemDtoFromDB = itemService.create(testUser.getId(), itemDto);

        ItemDto updateItemFromDB = itemService.update(testUser.getId(), itemDtoFromDB.getId(), updateItemDto);

        checkItemsAreTheSame(updateItemFromDB, updateItemDto);
    }

    @Test
    void getItemByIdTest() {
        ItemDto itemDtoFromDB = itemService.create(testUser.getId(), itemDto);

        ItemDto itemByIdFromDB = itemService.findById(testUser.getId(), itemDtoFromDB.getId());

        checkItemsAreTheSame(itemByIdFromDB, itemDto);
    }


    @Test
    void getAllUserItemsTest() {
        List<ItemDto> testList = List.of(itemDto, updateItemDto);
        for (ItemDto dto : testList) {
            itemService.create(testUser.getId(), dto);
        }

        List<ItemDto> itemsFromDB = itemService.findAllItemsOfUser(testUser.getId(), 0, 3);

        assertThat(itemsFromDB.size(), equalTo(testList.size()));
        for (ItemDto dto : testList) {
            assertThat(itemsFromDB, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(dto.getName())),
                    hasProperty("description", equalTo(dto.getDescription())),
                    hasProperty("available", equalTo(dto.getAvailable())))));
        }
    }

    @Test
    void findItemsTest() {
        String textSearch = "аккУМУляторная";
        List<ItemDto> testList = List.of(itemDto, updateItemDto);
        for (ItemDto dto : testList) {
            itemService.create(testUser.getId(), dto);
        }

        List<ItemDto> itemsFromSearch = itemService.search(textSearch, 0, 3);

        assertThat(itemsFromSearch.size(), equalTo(1));
        checkItemsAreTheSame(itemsFromSearch.get(0), updateItemDto);
    }

    @Test
    void addCommentTest() {
        CommentDto comment = CommentDto.builder()
                .text("Добавляем комментарий")
                .build();
        ItemDto itemDtoFromDB = itemService.create(testUser.getId(), itemDto);
        BookingShortDto bookingShortDto = BookingShortDto.builder()
                .start(LocalDateTime.now().plusNanos(1))
                .end(LocalDateTime.now().plusNanos(2))
                .itemId(itemDtoFromDB.getId())
                .build();

        BookingDto bookingDtoFromDB = bookingService.create(secondUserFromDB.getId(), bookingShortDto);
        bookingService.approve(testUser.getId(), bookingDtoFromDB.getId(), true);
        CommentDto commentFromDb = itemService.addComment(secondUserFromDB.getId(), itemDtoFromDB.getId(), comment);
        ItemDto itemWithComment = itemService.findById(testUser.getId(), itemDtoFromDB.getId());

        assertThat(commentFromDb.getId(), notNullValue());
        assertThat(commentFromDb.getText(), equalTo(comment.getText()));
        assertThat(commentFromDb.getAuthorName(), equalTo(secondUserDto.getName()));
        assertThat(commentFromDb.getCreated(), notNullValue());
        assertThat(itemWithComment.getComments().size(), equalTo(1));
        assertThat(itemWithComment.getComments().get(0).getText(), equalTo(comment.getText()));
        assertThat(itemWithComment.getComments().get(0).getAuthorName(), equalTo(secondUserDto.getName()));
        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.create(testUser.getId(), bookingShortDto));
        Assertions.assertEquals("The owner cannot be a booker.", exception.getMessage());
    }

    @Test
    void addCommentExceptionTest() {
        CommentDto comment = CommentDto.builder()
                .text("Добавляем комментарий")
                .build();
        ItemDto itemDtoFromDB = itemService.create(testUser.getId(), itemDto);
        BookingShortDto bookingShortDto = BookingShortDto.builder()
                .start(LocalDateTime.now().plusNanos(1))
                .end(LocalDateTime.now().plusNanos(2))
                .itemId(itemDtoFromDB.getId())
                .build();
        bookingService.create(secondUserFromDB.getId(), bookingShortDto);

        final BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                () -> itemService.addComment(secondUserFromDB.getId(), itemDtoFromDB.getId(), comment));
        Assertions.assertEquals("Error: Cannot add a comment because of empty booking list.", exception.getMessage());
    }

    @Test
    void updateItemWrongOwnerTest() {
        ItemDto itemDtoFromDB = itemService.create(testUser.getId(), itemDto);

        final ForbiddenAccessException exception = Assertions.assertThrows(ForbiddenAccessException.class,
                () -> itemService.update(secondUserFromDB.getId(), itemDtoFromDB.getId(), updateItemDto));
        Assertions.assertEquals("User with id=" + secondUserFromDB.getId() + " is not the owner.",
                exception.getMessage());
    }

    private void checkItemsAreTheSame(ItemDto itemDto, ItemDto secondItemDto) {
        assertThat(itemDto.getId(), notNullValue());
        assertThat(itemDto.getName(), equalTo(secondItemDto.getName()));
        assertThat(itemDto.getDescription(), equalTo(secondItemDto.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(secondItemDto.getAvailable()));
    }

}
