package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dao.UserDAO;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.validator.UserValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
public class UserServiceImpl implements UserService {
    //private final UserDAO userDAO;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        UserValidator.validateUser(userDto);
        checkEmail(userMapper.convertDtoToUser(userDto));

        User userFromResponse = userMapper.convertDtoToUser(userDto);
        //User createdUser = userDAO.createUser(userFromResponse);
        User createdUser = userRepository.save(userFromResponse);

        return userMapper.convertUserToDto(createdUser);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        Optional<String> userEmail = Optional.ofNullable(userDto.getEmail());
        User updatedUserFields = userMapper.convertDtoToUser(userDto);
        if (userEmail.isPresent()) {
            checkEmail(updatedUserFields, userId);
        }

        //User oldUser = userDAO.getUserById(userId);
        User oldUser = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id=" + userId + " не найден в БД"));

        User updatedUser = User.builder()
                .id(userId)
                .name(userDto.getName() != null ? userDto.getName() : oldUser.getName())
                .email(userDto.getEmail() != null ? userDto.getEmail() : oldUser.getEmail())
                .build();

        //User user = userDAO.updateUser(updatedUser);
        User user = userRepository.save(updatedUser);

        return userMapper.convertUserToDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        /*return userDAO.getAllUsers()
                .stream()
                .map(userMapper::convertUserToDto)
                .collect(Collectors.toList());*/
        return userRepository.findAll()
                .stream()
                .map(userMapper::convertUserToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long userId) {
        /*User user = Optional.ofNullable(userDAO.getUserById(userId)).orElseThrow(() ->
                new NotFoundException("Пользователь с id=" + userId + " не найден"));*/

        Optional<User> user = Optional.ofNullable(userRepository.findById(userId)).orElseThrow(() ->
                new NotFoundException("Пользователь с id=" + userId + " не найден"));

        return userMapper.convertUserToDto(user.get());
    }

    @Override
    public void deleteUser(Long id) {
        //userDAO.deleteUser(id);
        userRepository.deleteById(id);
    }

    private void checkEmail(User editedUser) {
       /* List<User> userList = userDAO.getAllUsers()
                .stream()
                .filter(u -> u.getEmail().equals(editedUser.getEmail()))
                .collect(Collectors.toList());*/

        List<User> userList = userRepository.findAll()
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
