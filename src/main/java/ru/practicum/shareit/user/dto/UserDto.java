package ru.practicum.shareit.user.dto;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


@Getter
@Setter
@ToString
public class UserDto {
    private Long id;
    @NotNull
    private String name;
    @Email
    @NotBlank
    private String email;
}
