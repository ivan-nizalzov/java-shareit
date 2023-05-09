package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.model.ItemRequest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class ItemRequestModelTest {

    @Test
    void testEqualAndHashCode() {
        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(1L);
        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setId(1L);

        Assertions.assertEquals(itemRequest1, itemRequest2);
        assertThat(itemRequest1.hashCode(), notNullValue());
    }

}
