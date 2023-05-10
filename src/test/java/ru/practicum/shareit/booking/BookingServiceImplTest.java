package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @InjectMocks
    private BookingServiceImpl bookingServiceImpl;
    @Mock
    private UserServiceImpl userServiceImpl;
    @Mock
    private ItemServiceImpl itemServiceImpl;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingMapperImpl bookingMapper;
    @Mock
    private UserMapperImpl userMapper;
    @Mock
    private ItemMapperImpl itemMapper;

    private final User user = new User(1L, "User", "user@email.com");
    private final UserDto userDto = new UserDto(1L, "User", "user@email.com");
    private final RequestBookingDto requestBookingDto = RequestBookingDto.builder()
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusHours(1L))
            .itemId(1L)
            .build();
    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Item")
            .description("Description")
            .available(true)
            .requestId(1L)
            .build();
    private final Item item = Item.builder()
            .id(1L)
            .name("Item")
            .description("Description")
            .available(true)
            .owner(user)
            .build();

    private final ResponseBookingDto responseBookingDto = ResponseBookingDto.builder()
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusHours(1L))
            .item(itemDto)
            .build();
    private final Booking booking = Booking.builder()
            .booker(user)
            .id(1L)
            .status(BookingStatus.APPROVED)
            .item(item).build();

    @Test
    void createBooking_whenTimeIsNotValid_thenReturnedTimeDataException() {
        RequestBookingDto bookingBadTime = RequestBookingDto.builder()
                .start(LocalDateTime.now().plusHours(1L))
                .end(LocalDateTime.now().minusHours(1L))
                .itemId(1L)
                .build();

        Exception e = assertThrows(TimeDataException.class,
                () -> bookingServiceImpl.create(bookingBadTime, 1L));

        assertEquals(e.getMessage(), String.format("Invalid booking time start = %s  end = %s",
                bookingBadTime.getStart(), bookingBadTime.getEnd()));
    }

    @Test
    void createBooking_whenUserIsNotOwner_thenReturnedOperationAccessException() {
        Mockito.when(userServiceImpl.findUserById(anyLong()))
                .thenReturn(userDto);
        Mockito.when(itemServiceImpl.findItemById(anyLong(), anyLong()))
                .thenReturn(itemDto);
        Mockito.when(itemServiceImpl.findOwnerId(anyLong()))
                .thenReturn(1L);
        Mockito.when(userMapper.toUser(userDto))
                        .thenReturn(user);
        Mockito.when(itemMapper.toItem(itemDto))
                        .thenReturn(item);

        final ForbiddenAccessException e = Assertions.assertThrows(ForbiddenAccessException.class,
                () -> bookingServiceImpl.create(requestBookingDto, 1L));

        assertEquals(e.getMessage(), "The owner cannot be a booker.");
    }

    @Test
    void createBooking_whenItemIsNotAvailable_thenReturnedNotAvailableException() {
        itemDto.setAvailable(false);
        item.setAvailable(false);

        Mockito.when(userServiceImpl.findUserById(anyLong()))
                .thenReturn(userDto);
        Mockito.when(itemServiceImpl.findItemById(anyLong(), anyLong()))
                .thenReturn(itemDto);
        Mockito.when(itemServiceImpl.findOwnerId(anyLong()))
                .thenReturn(2L);

        Mockito.when(userMapper.toUser(userDto))
                .thenReturn(user);
        Mockito.when(itemMapper.toItem(itemDto))
                .thenReturn(item);

        Exception e = assertThrows(NotAvailableException.class,
                () -> bookingServiceImpl.create(requestBookingDto, 1L));

        assertEquals(e.getMessage(), String.format("Item with id = " + item.getId() + " is not available.", 2L));
    }

    @Test
    void findBookingById_whenBookingIsNotFound_thenReturnedNotFoundException() {
        Mockito.when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Exception e = assertThrows(NotFoundException.class,
                () -> bookingServiceImpl.findBookingById(1L, 1L));

        assertEquals(e.getMessage(), String.format("Booking with id = %d not found.", 1L));
    }

    @Test
    void findBookingById_whenUserIsNotOwner_thenReturnedForbiddenAccessException() {
        Mockito.when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        Exception e = assertThrows(ForbiddenAccessException.class,
                () -> bookingServiceImpl.findBookingById(1L, 100L));

        assertEquals(e.getMessage(), String.format("User with id = %d is not the owner, access to booking is denied.", 100L));
    }

    @Test
    void getAllBookings_whenStateIsUnknown_thenReturnedBadRequestException() {
        Mockito.when(userServiceImpl.findUserById(anyLong()))
                .thenReturn(userDto);

        Exception e = assertThrows(BadRequestException.class,
                () -> bookingServiceImpl.findAllBookingsByUser("хslfs", 1L, 0, 10));

        assertEquals(e.getMessage(), "Unknown state: хslfs");
    }

    @Test
    void approve_whenBookingDecision_thenReturnedAlreadyExistsException() {
        responseBookingDto.setStatus(BookingStatus.APPROVED);

        Mockito.when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        Mockito.when(bookingServiceImpl.findBookingById(1L, 1L))
                        .thenReturn(responseBookingDto);
        Mockito.when(itemServiceImpl.findOwnerId(anyLong()))
                .thenReturn(1L);

        Exception e = assertThrows(AlreadyExistsException.class,
                () -> bookingServiceImpl.approve(1L, 1L, true));

        assertEquals(e.getMessage(), "The booking decision has already been made.");
    }

    @Test
    void approve_whenUserIsNotOwner_thenReturnedForbiddenAccessException() {
        Mockito.when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        Mockito.when(bookingServiceImpl.findBookingById(1l, 1l))
                        .thenReturn(responseBookingDto);
        Mockito.when(itemServiceImpl.findOwnerId(anyLong()))
                .thenReturn(2L);

        Exception e = assertThrows(ForbiddenAccessException.class,
                () -> bookingServiceImpl.approve(1L, 1L, true));

        assertEquals(e.getMessage(), String.format("User with id = %d is not the owner, no access to booking.", 1L));
    }

}
