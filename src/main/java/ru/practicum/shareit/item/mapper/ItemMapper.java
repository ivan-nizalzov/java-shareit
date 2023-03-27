package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.FIELD)
public interface ItemMapper {
    @Mapping(target = "owner", ignore = true)
    Item convertDtoToItem(ItemDto itemDto);
    ItemDto convertItemToDto(Item item);

}
