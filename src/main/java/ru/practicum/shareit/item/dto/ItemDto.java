package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.ShortBookingItemDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
public class ItemDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private ShortBookingItemDto lastBooking;
    private ShortBookingItemDto nextBooking;
    private List<CommentDto> comments;
}
