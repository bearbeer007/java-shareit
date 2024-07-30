package ru.practicum.shareit.item.comment.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {
    private Long id;
    @NotBlank
    private String text;
    private ItemRequestDto item;
    @Positive
    private Long authorId;
    private String authorName;
    private LocalDateTime created;
}
