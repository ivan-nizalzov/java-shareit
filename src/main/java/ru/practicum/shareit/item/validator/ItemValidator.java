package ru.practicum.shareit.item.validator;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

@Slf4j
public class ItemValidator {
    public static void validateItem(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            log.warn("У предмета с id={} не задано имя", itemDto.getId());
            throw new ValidationException("Имя предмета задано некорректно или отсутствует");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
            log.warn("У предмета с id={} не задано описание", itemDto.getId());
            throw new ValidationException("Описание предмета задано некорректно или отсутствует");
        }
        if (itemDto.getIsAvailable() == null) {
            log.warn("У предмета с id={} не задана доступность аренды", itemDto.getId());
            throw new ValidationException("Не указана доступность аренды предмета");
        }
    }

}
