package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class BookingModelTest {

    @Test
    void testEqualAndHashCode() {
        Booking booking1 = new Booking();
        booking1.setId(1L);
        Booking booking2 = new Booking();
        booking2.setId(1L);

        Assertions.assertEquals(booking1, booking2);
        assertThat(booking1.hashCode(), notNullValue());
    }

}
