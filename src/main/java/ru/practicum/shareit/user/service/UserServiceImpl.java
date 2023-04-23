package ru.practicum.shareit.user.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpl implements UserService {

  private final UserRepository storage;

  @Override
  public UserDto createUser(UserDto userDto) {
    checkEmailPresents(userDto);

    var user = storage.save(UserMapper.toUser(userDto));
    return UserMapper.toUserDto(user);
  }

  @Override
  public UserDto updateUser(long userId, UserDto userDto) {
    var userPreviousVersion = storage.findById(userId).orElseThrow();
    var updatedUser = User.builder()
        .id(userId)
        .name(userDto.getName() != null ? userDto.getName() : userPreviousVersion.getName())
        .email(userDto.getEmail() != null ? userDto.getEmail() : userPreviousVersion.getEmail())
        .build();

    var user = storage.save(updatedUser);
    return UserMapper.toUserDto(user);
  }

  @Override
  public UserDto getUser(long userId) {
    return storage.findById(userId).map(UserMapper::toUserDto)
        .orElseThrow(() -> new NoSuchElementException("User with id: " + userId + " doesn't exists"));
  }

  @Override
  public List<UserDto> getUsers() {
    return storage.findAll().stream()
        .map(UserMapper::toUserDto)
        .collect(Collectors.toList());
  }

  @Override
  public void deleteUser(long userId) {
    storage.deleteById(userId);
  }

  private void checkEmailPresents(UserDto userDto) {
    if (StringUtils.isBlank(userDto.getEmail())) {
      throw new IllegalStateException("Адрес электронной почты должен быть заполнен");
    }
  }
}
