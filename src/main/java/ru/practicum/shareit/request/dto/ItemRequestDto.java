package ru.practicum.shareit.request.dto;

import lombok.Data;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class ItemRequestDto {
    @NotBlank
    private String description;
    @FutureOrPresent
    private LocalDateTime created;
}