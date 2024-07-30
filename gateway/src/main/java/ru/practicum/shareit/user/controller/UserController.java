package ru.practicum.shareit.user.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.UserRequestDto;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> saveUser(@RequestBody @Valid UserRequestDto userRequestDto) {
        log.info("Получен запрос на сохранение пользователя {}", userRequestDto);
        return userClient.saveUser(userRequestDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable Long id) {
        log.info("Получен запрос на получение пользователя с id={}", id);
        return userClient.getUser(id);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Получен запрос на получение всех пользователей");
        return userClient.getUsers();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchUser(@RequestBody UserRequestDto userRequestDto, @PathVariable Long id) {
        log.info("Получен запрос на изменение пользователя {}, id={}", userRequestDto, id);
        return userClient.patchUser(userRequestDto, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        log.info("Получен запрос на удаление пользователя с id={}", id);
        return userClient.deleteUser(id);
    }
}
