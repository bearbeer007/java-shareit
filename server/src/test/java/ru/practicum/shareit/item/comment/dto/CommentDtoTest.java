package ru.practicum.shareit.item.comment.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class CommentDtoTest {
    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    @SneakyThrows
    void shouldCreateComment() {
        CommentDto comment = new CommentDto();
        comment.setText("text");

        JsonContent<CommentDto> jsonComment = json.write(comment);

        assertThat(jsonComment).extractingJsonPathStringValue("$.text").isEqualTo("text");
    }

    @Test
    @SneakyThrows
    void shouldResponse() {
        CommentDto comment = new CommentDto();
        comment.setId(1L);
        comment.setText("test");
        comment.setAuthorName("testName");
        comment.setCreated(LocalDateTime.of(2024, 1, 1, 1, 1, 1));

        JsonContent<CommentDto> jsonComment = json.write(comment);

        assertThat(jsonComment).extractingJsonPathStringValue("$.text").isEqualTo("test");
        assertThat(jsonComment).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonComment).extractingJsonPathStringValue("$.created")
                .isEqualTo("2024-01-01T01:01:01");
        assertThat(jsonComment).extractingJsonPathStringValue("$.authorName").isEqualTo("testName");
    }
}
