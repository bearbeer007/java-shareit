package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;
    private BookingDto expectedBooking;
    private UserDto expectedUser;
    private ItemDto expectedItem;

    private ItemRequest expectedItemRequest;

    @BeforeEach
    void setUp() {
        expectedBooking = new BookingDto();
        expectedBooking.setId(1L);
        expectedBooking.setStart(LocalDateTime.of(2024, 1, 1, 1, 1, 1));
        expectedBooking.setEnd(LocalDateTime.of(2024, 2, 1, 1, 1, 1));

        expectedUser = new UserDto();
        expectedUser.setId(2L);
        expectedUser.setName("testName");
        expectedUser.setEmail("testEmail@mail.ru");

        expectedItem = new ItemDto();
        expectedItem.setId(3L);
        expectedItem.setDescription("desc");
        expectedItem.setAvailable(true);
    }

    @Test
    void shouldAddRequestBooking() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setBooker(expectedUser);
        bookingDto.setItem(expectedItem);
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.of(2024, 1, 1, 1, 1, 1));
        bookingDto.setEnd(LocalDateTime.of(2024, 2, 1, 1, 1, 1));


        when(bookingService.requestBooking(any(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(expectedBooking))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedBooking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(expectedBooking.getStart().toString())))
                .andExpect(jsonPath("$.end", is(expectedBooking.getEnd().toString())));

    }

    @Test
    void shouldReturnBookingById() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setBooker(expectedUser);
        bookingDto.setItem(expectedItem);
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.of(2024, 1, 1, 1, 1, 1));
        bookingDto.setEnd(LocalDateTime.of(2024, 2, 1, 1, 1, 1));


        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())));
    }
}
