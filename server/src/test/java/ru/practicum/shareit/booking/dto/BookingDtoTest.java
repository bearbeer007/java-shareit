package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    private BookingDto bookingRequest;

    @Test
    @SneakyThrows
    void testBookingDto() {
        bookingRequest = new BookingDto();
        bookingRequest.setId(1L);
        bookingRequest.setStart(LocalDateTime.of(2024, 1, 1, 1, 1, 1));
        bookingRequest.setEnd(LocalDateTime.of(2024, 2, 2, 2, 2, 2));
        bookingRequest.setItemId(5L);
        JsonContent<BookingDto> result = json.write(bookingRequest);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2024-01-01T01:01:01");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2024-02-02T02:02:02");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(5);
    }
}
