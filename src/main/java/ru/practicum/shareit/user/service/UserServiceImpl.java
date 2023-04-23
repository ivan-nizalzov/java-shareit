package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.mapper.UserMapper.toUser;
import static ru.practicum.shareit.user.mapper.UserMapper.toUserDto;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        log.info("Created new User");
        return toUserDto(userRepository.save(toUser(userDto)));
    }

    @Transactional
    @Override
    public UserDto findUserById(Long userId) {
        log.info("Found User with id={}.", userId);
        return toUserDto(userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("User with id = %d not found.", userId))));
    }

    @Transactional
    @Override
    public List<UserDto> findAllUsers() {
        log.info("Found all of Users.");
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserDto update(UserDto userDto, Long userId) {
        User user = toUser(findUserById(userId));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        log.info("Updated User with id={}.", userId);

        return toUserDto(userRepository.save(user));
    }

    @Transactional
    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
        log.info("Deleted User with id={}.", userId);
    }
}
