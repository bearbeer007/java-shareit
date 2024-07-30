package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;
    private ItemRequestDto expectedItemRequestDto;

    @BeforeEach
    void setUp() {
        expectedItemRequestDto = new ItemRequestDto();
        expectedItemRequestDto.setId(1L);
        expectedItemRequestDto.setRequesterId(1L);
        expectedItemRequestDto.setDescription("description");
        expectedItemRequestDto.setCreated(LocalDateTime.of(2024, 1, 1, 1, 11, 11));
        expectedItemRequestDto.setItems(List.of());
    }

    @Test
    void shouldCreateNewItemRequest() throws Exception {

        when(itemRequestService.save(any(), anyLong()))
                .thenReturn(expectedItemRequestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(expectedItemRequestDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedItemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.requesterId", is(expectedItemRequestDto.getRequesterId()), Long.class))
                .andExpect(jsonPath("$.description", is(expectedItemRequestDto.getDescription()), String.class))
                .andExpect(jsonPath("$.created", is(expectedItemRequestDto.getCreated().toString()),
                        LocalDateTime.class));

        Mockito.verify(itemRequestService, Mockito.times(1))
                .save(any(), anyLong());
    }

    @Test
    public void shouldCreateWithoutUserId() throws Exception {
        mvc.perform(
                        post("/requests")
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldReturnRequestById() throws Exception {
        ItemRequestDto requestResponse = new ItemRequestDto();
        requestResponse.setDescription(expectedItemRequestDto.getDescription());
        requestResponse.setCreated(expectedItemRequestDto.getCreated());
        requestResponse.setItems(List.of());

        when(itemRequestService.getRequest(anyLong(), anyLong()))
                .thenReturn(requestResponse);

        mvc.perform(get("/requests/1").characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(requestResponse.getDescription()), String.class))
                .andExpect(jsonPath("$.created", is(requestResponse.getCreated().toString()), LocalDateTime.class))
                .andExpect(jsonPath("$.items", is(requestResponse.getItems()), List.class));

        Mockito.verify(itemRequestService)
                .getRequest(anyLong(), anyLong());

    }


}
