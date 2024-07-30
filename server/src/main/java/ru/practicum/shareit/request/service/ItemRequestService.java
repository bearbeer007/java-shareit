package ru.practicum.shareit.request.service;

import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto save(@Valid ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDto> getRequestsByRequester(Long userId);

    List<ItemRequestDto> getAll(Long userId, PageRequest pageRequest);

    ItemRequestDto getRequest(Long requestId, Long userId);
}