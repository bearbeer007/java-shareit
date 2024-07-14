package ru.practicum.shareit.user.dto;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


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
