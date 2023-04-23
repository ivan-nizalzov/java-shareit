package ru.practicum.shareit.user.dao;

import java.util.List;
import ru.practicum.shareit.user.model.User;

public interface UserDao {

  User createUser(User user);

  User updateUser(User user);

  User getUser(long userId);

  List<User> getUsers();

  void deleteUser(long userId);
}
