package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    //@Mapping(target = "itemRequest", source = "requestId", qualifiedByName = "mapRequestId")
    Item toItem(ItemDto itemDto);
    @Mapping(target = "requestId", source = "itemRequest.id")
    ItemDto toItemDto(Item item);

    @Named("mapRequestId")
    default ItemRequest mapRequestId(Long requestId) {
        if (requestId.equals(null)) {
            return null;
        }
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);

        return itemRequest;
    }

}