package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserDto userDto) {
        log.debug("POST-запрос на создание нового пользователя.");

        return ResponseEntity.ok(userService.create(userDto));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> findById(@PathVariable long userId) {
        log.debug("GET-запрос на вывод пользователя по идентификатору.");

        return ResponseEntity.ok(userService.findUserById(userId));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        log.debug("GET-запрос на вывод всех пользователей.");

        return ResponseEntity.ok(userService.findAllUsers());
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> update(@RequestBody UserDto userDto, @PathVariable long userId) {
        log.debug("PATCH-запрос на обновление пользователя.");

        return ResponseEntity.ok(userService.update(userDto, userId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable long userId) {
        log.debug("DELETE-запрос на удаление пользователя.");
        userService.delete(userId);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}