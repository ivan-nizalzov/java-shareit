package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserDAO {

    List<User> getAllUsers();
    User getUserById(Long id);
    User createUser(User user);
    User updateUser(User user);
    void deleteUser(Long id);

}
