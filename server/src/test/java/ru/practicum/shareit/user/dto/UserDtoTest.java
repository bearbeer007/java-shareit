package ru.practicum.shareit.user.dto;


import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    @SneakyThrows
    void testBookingDto() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@mail.ru");
        userDto.setName("testName");
        JsonContent<UserDto> result = json.write(userDto);
        System.out.println(result);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("testName");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("test@mail.ru");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);

    }
}
