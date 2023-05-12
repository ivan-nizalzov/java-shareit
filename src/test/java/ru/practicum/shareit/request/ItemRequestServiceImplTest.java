package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.servicce.ItemRequestService;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class ItemRequestServiceImplTest {
    private final ItemRequestService requestService;
    private final UserService userService;
    private UserDto firstUser;
    private UserDto secondUser;
    private ItemRequestDto itemRequestDto;
    private List<ItemRequestDto> itemRequestList;

    @BeforeEach
    public void setUp() {
        UserDto userDto = UserDto.builder()
                .email("test@test.com")
                .name("testName")
                .build();
        UserDto secondUserDto = UserDto.builder()
                .email("second@test.com")
                .name("secondName")
                .build();
        itemRequestDto = ItemRequestDto.builder()
                .description("Нужна новая вещь")
                .build();
        ItemRequestDto secondRequestDto = ItemRequestDto.builder()
                .description("Второй запрос на нужную вещь")
                .build();

        itemRequestList = List.of(itemRequestDto, secondRequestDto);

        firstUser = userService.create(userDto);
        secondUser = userService.create(secondUserDto);
    }

    @Test
    void createRequestTest() {
        ItemRequestDto requestDtoFromDB = requestService.create(firstUser.getId(), itemRequestDto);

        assertThat(requestDtoFromDB.getId(), notNullValue());
        assertThat(requestDtoFromDB.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(requestDtoFromDB.getCreated(), notNullValue());
    }

    @Test
    void getRequestByIdTest() {
        ItemRequestDto requestDtoFromDB = requestService.create(firstUser.getId(), itemRequestDto);
        ItemRequestDto requestById = requestService.findById(firstUser.getId(), requestDtoFromDB.getId());

        assertThat(requestById.getId(), equalTo(requestDtoFromDB.getId()));
        assertThat(requestById.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(requestById.getCreated(), equalTo(requestDtoFromDB.getCreated()));
    }

    @Test
    void getAllRequestsByIdTest() {
        for (ItemRequestDto itemRequest : itemRequestList) {
            requestService.create(firstUser.getId(), itemRequest);
        }

        List<ItemRequestDto> requestsOfUserList = requestService.findAllRequestsOfUser(firstUser.getId());

        assertThat(requestsOfUserList, hasSize(requestsOfUserList.size()));

        for (ItemRequestDto itemRequest : itemRequestList) {
            assertThat(requestsOfUserList, hasItem(allOf(
                    hasProperty("id"), notNullValue(),
                    hasProperty("description", equalTo(itemRequest.getDescription())),
                    hasProperty("created", notNullValue()))));
        }
    }

    @Test
    void getAllRequestsTest() {
        for (ItemRequestDto itemRequest : itemRequestList) {
            requestService.create(firstUser.getId(), itemRequest);
        }

        List<ItemRequestDto> allRequests = requestService.findAllRequestsExceptYours(secondUser.getId(), 0, 3);

        assertThat(allRequests, hasSize(itemRequestList.size()));
        for (ItemRequestDto itemRequest : itemRequestList) {
            assertThat(allRequests, hasItem(allOf(
                    hasProperty("id"), notNullValue(),
                    hasProperty("description", equalTo(itemRequest.getDescription())),
                    hasProperty("created", notNullValue()))));
        }
    }

    @Test
    void getRequestByIdWrongUserTest() {
        Long badId = 999L;

        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> requestService.create(badId, itemRequestDto));
        Assertions.assertEquals("При запросе пользователя произошла ошибка", exception.getMessage());
    }

    @Test
    void getRequestByIdWrongIdTest() {
        requestService.create(firstUser.getId(), itemRequestDto);
        Long badId = 999L;

        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> requestService.findById(firstUser.getId(), badId));
        Assertions.assertEquals("ItemRequest with id=" + badId + " not found.", exception.getMessage());
    }
}
