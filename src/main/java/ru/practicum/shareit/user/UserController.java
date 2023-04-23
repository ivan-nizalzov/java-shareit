package ru.practicum.shareit.user;

import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserController {

  private final UserService userService;

  @PostMapping
  public UserDto createUser(@RequestBody @Valid UserDto userDto) {
    return userService.createUser(userDto);
  }

  @PatchMapping("/{userId}")
  public UserDto updateUser(@PathVariable long userId, @RequestBody @Valid UserDto userDto) {
    return userService.updateUser(userId, userDto);
  }

  @GetMapping("/{userId}")
  public UserDto getUser(@PathVariable long userId) {
    return userService.getUser(userId);
  }

  @GetMapping
  public List<UserDto> getUsers() {
    return userService.getUsers();
  }

  @DeleteMapping("/{userId}")
  public void deleteUser(@PathVariable long userId) {
    userService.deleteUser(userId);
  }
}
