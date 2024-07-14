package ru.practicum.shareit.item.service.interfaces;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId);

    ItemDto getItemById(Long userId, Long itemId);

    List<ItemDto> getUserItems(Long userId);

    List<ItemDto> searchItemToRent(Long userId, String text);

    CommentDto createComment(CommentDto commentDto, Long itemId, Long userId);
}
