package ru.practicum.shareit.user.service;

import java.util.List;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

  UserDto createUser(UserDto userDto);

  UserDto updateUser(long userId, UserDto userDto);

  UserDto getUser(long userId);

  List<UserDto> getUsers();

  void deleteUser(long userId);
}
