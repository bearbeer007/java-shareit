package ru.practicum.shareit.user.dto;

import lombok.experimental.UtilityClass;
import org.mapstruct.*;
import ru.practicum.shareit.common.AbstractMapper;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserDto userDto);

    UserDto toUserDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void updateUserFromUserDto(UserDto userDto, @MappingTarget User user);
}