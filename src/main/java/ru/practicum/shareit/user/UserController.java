package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.interfaces.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        log.info("Поступил POST-запрос на добавление user");
        return userService.addUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        log.info("Поступил PATCH-запрос на обновление user с id = {}", id);
        return userService.updateUser(id, userDto);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Поступил GET-запрос на получение всех users");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto findUserById(@PathVariable Long id) {
        log.info("Поступил GET-запрос на получение user c id = {}", id);
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Long id) {
        log.info("Поступил DELETE-запрос на удаление user c id = {}", id);
        userService.deleteUserById(id);
    }
}
