package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.Constant;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.interfaces.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(Constant.HEADER_USER_ID) Long userId) {
        log.info("Поступил POST-запрос на добавление item от user c id = {}", userId);
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId, @RequestBody ItemDto itemDto, @RequestHeader(Constant.HEADER_USER_ID) Long userId) {
        log.info("Поступил PATCH-запрос от user c id = {} на обновление item с id = {}", userId, itemId);
        return itemService.updateItem(itemId, itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemInfo(@RequestHeader(Constant.HEADER_USER_ID) Long userId, @PathVariable Long itemId) {
        log.info("Поступил GET-запрос на получение item с id = {} от user с id = {}", itemId, userId);
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getUserItems(@RequestHeader(Constant.HEADER_USER_ID) Long userId) {
        log.info("Поступил GET-запрос на получение всех user items c userId = {}", userId);
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> findItemToRent(@RequestHeader(Constant.HEADER_USER_ID) Long userId, @RequestParam(required = false) String text) {
        log.info("Поступил GET-запрос от user c id = {} на поиск item", userId);
        return itemService.searchItemToRent(userId, text);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto createComment(@Valid @RequestBody CommentDto commentDto, @PathVariable Long itemId, @RequestHeader(Constant.HEADER_USER_ID) Long userId) {
        log.info("Поступил POST-запрос на добавление комментария вещи item c id = {}, пользователем user c id = {}", itemId, userId);
        return itemService.createComment(commentDto, itemId, userId);
    }
}
