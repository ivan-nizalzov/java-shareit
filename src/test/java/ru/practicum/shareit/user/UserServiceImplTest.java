package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolationException;
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
public class UserServiceImplTest {
    private final UserService userService;
    private final UserMapper userMapper;
    private final EntityManager em;
    private UserDto userDto;
    private UserDto secondUserDto;

    @BeforeEach
    public void setUp() {
        userDto = UserDto.builder()
                .email("test@test.com")
                .name("testName")
                .build();

        secondUserDto = UserDto.builder()
                .email("second@test.com")
                .name("secondName")
                .build();
    }

    @Test
    void createUserTest() {
        userService.create(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User testUser = query.setParameter("email", userDto.getEmail())
                .getSingleResult();

        checkUsersAreTheSame(testUser, userDto, 1L);
    }

    @Test
    void updateUserTest() {
        userService.create(userDto);
        UserDto updateUser = UserDto.builder()
                .email("update@test.com")
                .name("updateName")
                .build();
        userService.update(1L, updateUser);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User testUser = query.setParameter("email", updateUser.getEmail())
                .getSingleResult();

        checkUsersAreTheSame(testUser, updateUser, 1L);
    }

    @Test
    void getUserByIdTest() {
        UserDto testUser = userService.create(userDto);
        UserDto userFromDB = userService.findById(testUser.getId());

        checkUsersAreTheSame(userMapper.toUser(userFromDB), userDto, 1L);
    }

    @Test
    void getAllUsersTest() {
        List<UserDto> testList = List.of(userDto, secondUserDto);
        for (UserDto dto : testList) {
            userService.create(dto);
        }

        List<UserDto> dtoFromDB = userService.findAllUsers();

        assertThat(dtoFromDB.size(), equalTo(testList.size()));
        for (UserDto user : testList) {
            assertThat(dtoFromDB, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(user.getName())),
                    hasProperty("email", equalTo(user.getEmail())))));
        }
    }

    @Test
    void deleteUserTest() {
        userService.create(userDto);
        UserDto userFromDB = userService.findAllUsers().get(0);

        userService.delete(userFromDB.getId());
        List<UserDto> dtoFromDB = userService.findAllUsers();

        assertThat(dtoFromDB.size(), equalTo(0));
        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> userService.findById(userFromDB.getId()));
        Assertions.assertEquals("User with id=" + userFromDB.getId() + " not found.", exception.getMessage());
    }

    @Test
    void createUserInvalidEmailTest() {
        UserDto userDtoBadEmail = userDto = UserDto.builder()
                .email("bademail.com")
                .name("testName")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> userService.create(userDtoBadEmail));
    }

    private void checkUsersAreTheSame(User user, UserDto userDto, Long id) {
        assertThat(user.getId(), equalTo(id));
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }
}



