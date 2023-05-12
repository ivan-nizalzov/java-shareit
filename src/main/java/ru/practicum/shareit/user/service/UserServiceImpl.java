package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        User newUser = userMapper.toUser(userDto);
        User user = userRepository.save(newUser);
        log.info("Created new user with id={}.", user.getId());

        return userMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User updateUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found."));

        ofNullable(userDto.getName()).ifPresent(updateUser::setName);
        ofNullable(userDto.getEmail()).ifPresent(updateUser::setEmail);

        log.info("Updated user with id={}.", userId);

        return userMapper.toUserDto(userRepository.save(updateUser));
    }

    @Override
    public UserDto findById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found."));
        log.info("Found user with id={}.", userId);

        return userMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> userList = userRepository.findAll();
        log.info("Found all users ({}).", userList.size());
        return userList.stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
        log.info("Deleted user with id={}.", userId);
    }

}
