package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

public class UserDtoMapper {

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User dtoToUser(UserDto userDto) {
        User user = User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();

        return user;
    }

}
