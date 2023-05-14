package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {

    @Mapping(target = "requestorId", source = "itemRequest.requestor.id")
    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);

    @Mapping(target = "id", source = "itemRequestDto.id")
    @Mapping(target = "description", source = "itemRequestDto.description")
    @Mapping(target = "created", source = "dateTime")
    @Mapping(target = "requestor", source = "user")
    ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user, LocalDateTime dateTime);

}
