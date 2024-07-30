package ru.practicum.shareit.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestGatewayDto;



@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody @Valid ItemRequestGatewayDto itemRequestDto,
                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на сохранение запроса");
        return itemRequestClient.saveRequest(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByRequester(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение списка запросов пользователя id={}", userId);
        return itemRequestClient.getRequestsByRequester(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получен запрос на получение списка запросов");
        if (from < 0 || size < 0) {
            throw new IllegalArgumentException("Некорректные параметры");
        }
        return itemRequestClient.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@PathVariable Long requestId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение запроса id={}", requestId);
        return itemRequestClient.getRequest(requestId, userId);
    }
}
