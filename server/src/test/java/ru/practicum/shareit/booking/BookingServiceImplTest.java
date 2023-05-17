package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.UnsupportedStatus;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
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
public class BookingServiceImplTest {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private UserDto testUser;
    private UserDto secondTestUser;
    private ItemDto itemDtoFromDB;
    private BookingShortDto bookingShortDto;
    private BookingShortDto secondBookingShortDto;

    @BeforeEach
    public void setUp() {
        ItemDto itemDto = ItemDto.builder()
                .name("Дрель")
                .description("Простая дрель")
                .available(true)
                .build();

        UserDto userDto = UserDto.builder()
                .email("test@test.com")
                .name("testName")
                .build();

        UserDto secondUserDto = UserDto.builder()
                .email("second@test.com")
                .name("secondName")
                .build();

        testUser = userService.create(userDto);
        secondTestUser = userService.create(secondUserDto);
        itemDtoFromDB = itemService.create(testUser.getId(), itemDto);

        bookingShortDto = BookingShortDto.builder()
                .start(LocalDateTime.now().plusNanos(1))
                .end(LocalDateTime.now().plusNanos(2))
                .itemId(itemDtoFromDB.getId())
                .build();
        secondBookingShortDto = BookingShortDto.builder()
                .start(LocalDateTime.now().plusHours(3))
                .end(LocalDateTime.now().plusHours(4))
                .itemId(itemDtoFromDB.getId())
                .build();
    }

    @Test
    void createBookingTest() {
        BookingDto bookingDtoFromDB = bookingService.create(secondTestUser.getId(), bookingShortDto);

        assertThat(bookingDtoFromDB.getId(), notNullValue());
        checkBookingsAreTheSame(bookingDtoFromDB, bookingShortDto, secondTestUser, itemDtoFromDB, BookingStatus.WAITING);
    }

    @Test
    void approveBookingTest() {
        BookingDto bookingDtoFromDB = bookingService.create(secondTestUser.getId(), bookingShortDto);
        BookingDto approveBooking = bookingService.approve(testUser.getId(), bookingDtoFromDB.getId(), true);

        checkBookingsAreTheSame(approveBooking, bookingShortDto, secondTestUser, itemDtoFromDB, BookingStatus.APPROVED);
    }


    @Test
    void getBookingByIdTest() {
        BookingDto bookingDtoFromDB = bookingService.create(secondTestUser.getId(), bookingShortDto);
        BookingDto approveBooking = bookingService.approve(testUser.getId(), bookingDtoFromDB.getId(), true);
        BookingDto bookingById = bookingService.findById(testUser.getId(), approveBooking.getId());

        checkBookingsAreTheSame(bookingById, bookingShortDto, secondTestUser, itemDtoFromDB, BookingStatus.APPROVED);

        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.findById(999L, approveBooking.getId()));
        Assertions.assertEquals("The booker cannot be an owner.", exception.getMessage());
    }

    @Test
    void getAllBookingsTest() {
        List<BookingShortDto> bookingDtos = List.of(bookingShortDto, secondBookingShortDto);
        BookingDto firstBooking = bookingService.create(secondTestUser.getId(), bookingShortDto);
        bookingService.approve(testUser.getId(), firstBooking.getId(), true);
        BookingDto secondBooking = bookingService.create(secondTestUser.getId(), secondBookingShortDto);
        List<BookingDto> bookings = bookingService.findAllBookingsMadeByUser(secondTestUser.getId(), "ALL", 0, 3);

        assertThat(bookings.size(), equalTo(bookingDtos.size()));

        for (BookingShortDto dto : bookingDtos) {
            assertThat(bookings, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("start", equalTo(dto.getStart())),
                    hasProperty("end", equalTo(dto.getEnd())))));
        }

        List<BookingDto> approvedBookings = bookingService.findAllBookingsMadeByUser(
                secondTestUser.getId(), "WAITING", 0, 3);

        BookingDto waitingBooking = approvedBookings.get(0);

        assertThat(approvedBookings.size(), equalTo(1));
        assertThat(waitingBooking.getId(), equalTo(secondBooking.getId()));
        checkBookingsAreTheSame(waitingBooking, secondBookingShortDto, secondTestUser, itemDtoFromDB, BookingStatus.WAITING);
    }

    @Test
    void getAllOwnerBookingsTest() {
        List<BookingShortDto> bookingDtos = List.of(bookingShortDto, secondBookingShortDto);
        BookingDto firstBooking = bookingService.create(secondTestUser.getId(), bookingShortDto);
        bookingService.approve(testUser.getId(), firstBooking.getId(), true);
        BookingDto secondBooking = bookingService.create(secondTestUser.getId(), secondBookingShortDto);

        List<BookingDto> bookings = bookingService.findAllBookingsOfItemsOwner(testUser.getId(), "ALL", 0, 3);

        assertThat(bookings.size(), equalTo(bookingDtos.size()));
        for (BookingShortDto dto : bookingDtos) {
            assertThat(bookings, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("start", equalTo(dto.getStart())),
                    hasProperty("end", equalTo(dto.getEnd())))));
        }

        List<BookingDto> approvedBookings = bookingService.findAllBookingsOfItemsOwner(
                testUser.getId(), "WAITING", 0, 3);

        BookingDto waitingBooking = approvedBookings.get(0);

        assertThat(approvedBookings.size(), equalTo(1));
        assertThat(waitingBooking.getId(), equalTo(secondBooking.getId()));
        checkBookingsAreTheSame(waitingBooking, secondBookingShortDto, secondTestUser, itemDtoFromDB, BookingStatus.WAITING);
    }

    @Test
    void approveBookingWrongOwnerTest() {
        BookingDto bookingDtoFromDB = bookingService.create(secondTestUser.getId(), bookingShortDto);

        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.approve(secondTestUser.getId(), bookingDtoFromDB.getId(), true));
        Assertions.assertEquals("User with id=" + secondTestUser.getId() + " is not the owner of item with id="
                + bookingDtoFromDB.getItem().getId(), exception.getMessage());
    }

    @Test
    void approveBookingTwiceErrorTest() {
        BookingDto bookingDtoFromDB = bookingService.create(secondTestUser.getId(), bookingShortDto);
        bookingService.approve(testUser.getId(), bookingDtoFromDB.getId(), true);

        final BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                () -> bookingService.approve(testUser.getId(), bookingDtoFromDB.getId(), true));
        Assertions.assertEquals("The decision has been already made.", exception.getMessage());
    }

    @Test
    void getAllBookingsNonExistentStateTest() {
        String nonExistentState = "nonExistentState";
        bookingService.create(secondTestUser.getId(), bookingShortDto);

        final UnsupportedStatus exception = Assertions.assertThrows(UnsupportedStatus.class,
                () -> bookingService.findAllBookingsMadeByUser(secondTestUser.getId(), nonExistentState, 0, 3));
        Assertions.assertEquals("Unknown state: " + nonExistentState, exception.getMessage());
    }

    @Test
    void getAllBookingsRejectedStateTest() {
        BookingDto firstBooking = bookingService.create(secondTestUser.getId(), bookingShortDto);
        bookingService.approve(testUser.getId(), firstBooking.getId(), false);

        List<BookingDto> rejectedBookings = bookingService.findAllBookingsMadeByUser(
                secondTestUser.getId(), "REJECTED", 0, 3);

        BookingDto rejectedBooking = rejectedBookings.get(0);

        assertThat(rejectedBookings.size(), equalTo(1));
        checkBookingsAreTheSame(rejectedBooking, bookingShortDto, secondTestUser, itemDtoFromDB, BookingStatus.REJECTED);
    }

    @Test
    void getAllBookingsCurrentStateTest() {
        BookingShortDto bookingDto = BookingShortDto.builder()
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .itemId(itemDtoFromDB.getId())
                .build();
        List<BookingShortDto> bookingDtos = List.of(bookingDto);
        BookingDto firstBooking = bookingService.create(secondTestUser.getId(), bookingDto);
        bookingService.approve(testUser.getId(), firstBooking.getId(), true);

        List<BookingDto> currentBookings = bookingService.findAllBookingsMadeByUser(
                secondTestUser.getId(), "CURRENT", 0, 3);

        BookingDto currentBooking = currentBookings.get(0);

        assertThat(currentBookings.size(), equalTo(bookingDtos.size()));
        checkBookingsAreTheSame(currentBooking, bookingDto, secondTestUser, itemDtoFromDB, BookingStatus.APPROVED);
    }

    @Test
    void getAllBookingsFutureStateTest() {
        BookingShortDto bookingDto = BookingShortDto.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .itemId(itemDtoFromDB.getId())
                .build();
        List<BookingShortDto> bookingDtos = List.of(bookingDto);
        BookingDto firstBooking = bookingService.create(secondTestUser.getId(), bookingDto);

        List<BookingDto> futureBookings = bookingService.findAllBookingsMadeByUser(
                secondTestUser.getId(), "FUTURE", 0, 3);

        BookingDto futureBooking = futureBookings.get(0);

        assertThat(futureBookings.size(), equalTo(bookingDtos.size()));
        assertThat(futureBooking.getId(), equalTo(firstBooking.getId()));
        checkBookingsAreTheSame(futureBooking, bookingDto, secondTestUser, itemDtoFromDB, BookingStatus.WAITING);
    }

    @Test
    void getAllBookingsPastStateTest() {
        BookingShortDto bookingDto = BookingShortDto.builder()
                .start(LocalDateTime.now().minusHours(2))
                .end(LocalDateTime.now().minusHours(1))
                .itemId(itemDtoFromDB.getId())
                .build();
        List<BookingShortDto> bookingDtos = List.of(bookingDto);
        BookingDto firstBooking = bookingService.create(secondTestUser.getId(), bookingDto);
        bookingService.approve(testUser.getId(), firstBooking.getId(), true);

        List<BookingDto> pastBookings = bookingService.findAllBookingsMadeByUser(secondTestUser.getId(), "PAST", 0, 3);
        BookingDto pastBooking = pastBookings.get(0);

        assertThat(pastBookings.size(), equalTo(bookingDtos.size()));
        assertThat(pastBooking.getId(), equalTo(firstBooking.getId()));
        checkBookingsAreTheSame(pastBooking, bookingDto, secondTestUser, itemDtoFromDB, BookingStatus.APPROVED);
    }

    @Test
    void getAllOwnerBookingsCurrentStateTest() {
        BookingShortDto bookingDto = BookingShortDto.builder()
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .itemId(itemDtoFromDB.getId())
                .build();
        List<BookingShortDto> bookingDtos = List.of(bookingDto);
        BookingDto firstBooking = bookingService.create(secondTestUser.getId(), bookingDto);
        bookingService.approve(testUser.getId(), firstBooking.getId(), true);

        List<BookingDto> currentBookings = bookingService.findAllBookingsOfItemsOwner(testUser.getId(), "CURRENT", 0, 3);
        BookingDto currentBooking = currentBookings.get(0);

        assertThat(currentBookings.size(), equalTo(bookingDtos.size()));
        assertThat(currentBooking.getId(), equalTo(firstBooking.getId()));
        checkBookingsAreTheSame(currentBooking, bookingDto, secondTestUser, itemDtoFromDB, BookingStatus.APPROVED);
    }

    @Test
    void getAllOwnerBookingsFutureStateTest() {
        BookingShortDto bookingDto = BookingShortDto.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .itemId(itemDtoFromDB.getId())
                .build();
        List<BookingShortDto> bookingDtos = List.of(bookingDto);
        BookingDto firstBooking = bookingService.create(secondTestUser.getId(), bookingDto);
        bookingService.approve(testUser.getId(), firstBooking.getId(), true);

        List<BookingDto> futureBookings = bookingService.findAllBookingsOfItemsOwner(testUser.getId(), "FUTURE", 0, 3);
        BookingDto futureBooking = futureBookings.get(0);

        assertThat(futureBookings.size(), equalTo(bookingDtos.size()));
        assertThat(futureBooking.getId(), equalTo(firstBooking.getId()));
        checkBookingsAreTheSame(futureBooking, bookingDto, secondTestUser, itemDtoFromDB, BookingStatus.APPROVED);
    }

    @Test
    void getAllOwnerBookingsPastStateTest() {
        BookingShortDto bookingDto = BookingShortDto.builder()
                .start(LocalDateTime.now().minusHours(2))
                .end(LocalDateTime.now().minusHours(1))
                .itemId(itemDtoFromDB.getId())
                .build();
        List<BookingShortDto> bookingDtos = List.of(bookingDto);
        BookingDto firstBooking = bookingService.create(secondTestUser.getId(), bookingDto);
        bookingService.approve(testUser.getId(), firstBooking.getId(), true);

        List<BookingDto> pastBookings = bookingService.findAllBookingsOfItemsOwner(testUser.getId(), "PAST", 0, 3);
        BookingDto pastBooking = pastBookings.get(0);

        assertThat(pastBookings.size(), equalTo(bookingDtos.size()));
        assertThat(pastBooking.getId(), equalTo(firstBooking.getId()));
        checkBookingsAreTheSame(pastBooking, bookingDto, secondTestUser, itemDtoFromDB, BookingStatus.APPROVED);
    }

    @Test
    void getAllOwnerBookingsUserHasNothingTest() {
        final BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                () -> bookingService.findAllBookingsOfItemsOwner(secondTestUser.getId(), "ALL", 0, 3));
        Assertions.assertEquals("У пользователя нет ни одной вещи!", exception.getMessage());
    }

    @Test
    void createBookingItemStartLaterThanFinishTest() {
        BookingShortDto bookingDto = BookingShortDto.builder()
                .start(LocalDateTime.now().plusHours(2))
                .end(LocalDateTime.now().plusHours(1))
                .itemId(itemDtoFromDB.getId())
                .build();

        final BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                () -> bookingService.create(secondTestUser.getId(), bookingDto));
        Assertions.assertEquals("Invalid booking start time and end time.", exception.getMessage());
    }

    private void checkBookingsAreTheSame(
            BookingDto booking, BookingShortDto secondBooking, UserDto user, ItemDto item, BookingStatus status) {
        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStatus(), equalTo(status));
        assertThat(booking.getStart(), equalTo(secondBooking.getStart()));
        assertThat(booking.getEnd(), equalTo(secondBooking.getEnd()));
        assertThat(booking.getBooker().getId(), equalTo(user.getId()));
        assertThat(booking.getItem().getId(), equalTo(item.getId()));
        assertThat(booking.getItem().getName(), equalTo(item.getName()));
    }

}
