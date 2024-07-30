package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> create(@Valid @RequestBody CreateItemRequest itemRequest, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Post-запрос на добавление вещи");
        return ResponseEntity.ok().body(itemService.create(itemRequest, userId));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(@RequestBody ItemDto itemDto, @PathVariable Long itemId,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Patch-запрос на обновление вещи с id={}", itemId);
        return ResponseEntity.ok().body(itemService.update(itemDto, itemId, userId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get-запрос на получение вещи c id={}", itemId);
        return ResponseEntity.ok().body(itemService.getItemById(itemId, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllUserItems(@RequestHeader("X-Sharer-User-Id") Long id) {
        log.info("Get-запрос на получение вещей пользователя с id={}", id);
        return ResponseEntity.ok().body(itemService.getAllUserItems(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItem(@RequestParam String text) {
        log.info("Get-запрос на поиск вещи по запросу «{}»", text);
        return ResponseEntity.ok().body(itemService.searchItem(text));
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<CommentDto> postComment(@RequestBody @Valid CommentDto commentDto, @PathVariable Long itemId,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Post запрос на добавление комментария для вещи id={}, от пользователя id={}",
                itemId, userId);
        return ResponseEntity.ok().body(itemService.postComment(commentDto, itemId, userId));
    }
}
