package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class UserModelTest {

    @Test
    void testEqualAndHashCode() {
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(1L);

        Assertions.assertEquals(user1, user2);
        assertThat(user1.hashCode(), notNullValue());
    }

}
