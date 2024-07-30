package ru.practicum.shareit.item.service;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    ItemDto toItemDto(Item item);

    Item toItem(ItemDto itemDto);

    Item fromCreateItemRequest(CreateItemRequest itemRequest);
}
