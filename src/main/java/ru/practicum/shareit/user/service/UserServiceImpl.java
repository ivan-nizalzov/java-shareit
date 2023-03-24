package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.validator.UserValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        UserValidator.validateUser(userDto);
        checkEmail(UserDtoMapper.dtoToUser(userDto));

        User user = userRepository.createUser(UserDtoMapper.dtoToUser(userDto));

        return UserDtoMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        Optional<String> userEmail = Optional.ofNullable(userDto.getEmail());
        if (userEmail.isPresent()) {
            checkEmail(UserDtoMapper.dtoToUser(userDto), userId);
        }

        User oldUser = userRepository.getUserById(userId);

        User updatedUser = User.builder()
                .id(userId)
                .name(userDto.getName() != null ? userDto.getName() : oldUser.getName())
                .email(userDto.getEmail() != null ? userDto.getEmail() : oldUser.getEmail())
                .build();

        User user = userRepository.updateUser(updatedUser);

        return UserDtoMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers()
                .stream()
                .map(UserDtoMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = Optional.ofNullable(userRepository.getUserById(userId)).orElseThrow(() ->
                new NotFoundException("Пользователь с id=" + userId + " не найден"));

        return UserDtoMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteUser(id);
    }

    private void checkEmail(User editedUser) {
        List<User> userList = userRepository.getAllUsers()
                .stream()
                .filter(u -> u.getEmail().equals(editedUser.getEmail()))
                .collect(Collectors.toList());

        if (!userList.isEmpty()) {
            throw new AlreadyExistsEmailException("Адрес " + editedUser.getEmail() + " уже зарегистрирован!");
        }
    }

    private void checkEmail(User editedUser, Long userId) {
        if (!editedUser.getEmail().equals(getUserById(userId).getEmail())) {
            checkEmail(editedUser);
        }
    }

}
