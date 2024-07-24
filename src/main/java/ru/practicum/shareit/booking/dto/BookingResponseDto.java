package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class BookingResponseDto {
    private Long id;
    private ItemDto item;
    private LocalDateTime start;
    private LocalDateTime end;
    private UserDto booker;
    private Status status;
}
