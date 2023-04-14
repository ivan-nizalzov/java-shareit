package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@RequiredArgsConstructor
@Component
public class InMemoryUserDAO implements UserDAO {
    private final Map<Long, User> users;
    private static final AtomicLong id = new AtomicLong(0);

    @Override
    public User createUser(User user) {
        user.setId(id.incrementAndGet());
        users.put(user.getId(), user);
        log.info("Создан новый пользователь {}", user);

        return users.get(user.getId());
    }

    @Override
    public User updateUser(User user) {
        users.remove(user.getId());
        users.put(user.getId(), user);
        log.info("Обновлен пользователь с id={}", user.getId());

        return users.get(user.getId());
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Получен список всех пользователей, их кол-во: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Long userId) {
        log.info("Получен пользователь с userId={}", userId);
        User user = users.get(userId) != null ? users.get(userId) : null;
        return user;
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("Удален пользователь с userId={}", userId);
        users.remove(userId);
    }

}
