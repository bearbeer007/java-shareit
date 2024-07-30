package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(CreateItemRequest itemRequest, Long userId);


    ItemDto update(ItemDto itemDto, Long itemId, Long userId);

    ItemDto getItemById(Long itemId, Long userId);


    List<ItemDto> getAllUserItems(Long id);


    List<ItemDto> searchItem(String text);


    CommentDto postComment(CommentDto commentDto, Long itemId, Long userId);
}