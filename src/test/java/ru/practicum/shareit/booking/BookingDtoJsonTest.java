package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoJsonTest {
    @Autowired
    private JacksonTester<ResponseBookingDto> json;

    @Test
    void testItemDto() throws IOException {
        LocalDateTime dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        ItemDto item = ItemDto.builder()
                .id(1L)
                .name("Item")
                .description("Item description")
                .available(true)
                .build();

        UserDto userDto = UserDto.builder()
                .id(1L)
                .email("test@test.com")
                .name("testName")
                .build();

        ResponseBookingDto bookingDto = ResponseBookingDto.builder()
                .id(1L)
                .start(dateTime.plusSeconds(1))
                .end(dateTime.plusSeconds(2))
                .item(item)
                .booker(userDto)
                .status(BookingStatus.WAITING)
                .build();

        JsonContent<ResponseBookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(dateTime.plusSeconds(1).toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(dateTime.plusSeconds(2).toString());
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Item");
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("Item description");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("testName");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("test@test.com");
    }

}
