package ru.practicum.shareit.request.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;




@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestGatewayDto {
    @NotBlank
    private String description;
}
