package ru.practicum.shareit.user.validator;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.regex.Pattern;

@Slf4j
public class UserValidator {
    public static final String REGEX_PATTERN = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    public static void validateUser(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            log.warn("У юзера с id={} не указана эл.почта", userDto.getId());
            throw new ValidationException("Не указан адрес электронной почты");
        }
        if (!patternMatches(userDto.getEmail(), REGEX_PATTERN)) {
            log.warn("У юзера с id={} некорректно указан адрес эл.почты: {}", userDto.getId(), userDto.getEmail());
            throw new ValidationException("В адресе эл.почты недопустимые символы или отсутствует знак @");
        }
    }

    public static boolean patternMatches(String emailAddress, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }

}
