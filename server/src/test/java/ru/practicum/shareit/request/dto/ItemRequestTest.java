package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemRequestTest {
    @Autowired
    private JacksonTester<ItemRequest> json;
    private final User requester = new User(1L, "testName", "testEmail@mail");
    private final ItemRequest response = new ItemRequest(1L, "desc", requester, null, null);

    @Test
    @SneakyThrows
    void shouldItemRequest() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("description");
        JsonContent<ItemRequest> result = json.write(itemRequest);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("description");

    }

    @Test
    @SneakyThrows
    void shouldResponse() {
        JsonContent<ItemRequest> result = json.write(response);
        System.out.println(result);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("desc");
        assertThat(result).extractingJsonPathNumberValue("$.requester.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.requester.name").isEqualTo("testName");
        assertThat(result).extractingJsonPathStringValue("$.requester.email").isEqualTo("testEmail@mail");
        assertThat(result).extractingJsonPathStringValue("$.items").isEqualTo(null);

    }
}
