package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;



@Builder
@Data
@AllArgsConstructor
public class User {
    private Long id;  // уникальный идентификатор пользователя
    @NotBlank
    private String name; // имя или логин пользователя
    @NotBlank
    @Email
    private String email; // адрес электронной почты. Уникален для каждого пользователя.
}
