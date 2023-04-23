package ru.practicum.shareit.user.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

@Slf4j
@Component
public class UserInMemoryDaoImpl implements UserDao {

  private final Map<Long, User> users = new HashMap<>();
  private Long idsCounter = 0L;

  @Override
  public User createUser(User user) {
    user.setId(++idsCounter);
    users.put(user.getId(), user);
    log.info("Создан user: {}", user);

    return users.get(user.getId());
  }

  @Override
  public User updateUser(User user) {
    users.put(user.getId(), user);
    log.info("Обновлен user: {}", user);

    return users.get(user.getId());
  }

  @Override
  public User getUser(long userId) {
    return users.get(userId);
  }

  @Override
  public List<User> getUsers() {
    return new ArrayList<>(users.values());
  }

  @Override
  public void deleteUser(long userId) {
    users.remove(userId);
  }
}
