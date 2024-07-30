package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestMapper mapper;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemRequestDto save(ItemRequestDto itemRequestDto, Long userId) {
        ItemRequest itemRequest = mapper.toItemRequest(itemRequestDto);
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());
        return mapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getRequestsByRequester(Long userId) {
        userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException("Пользователь не найден"));
        return itemRequestRepository.findAllByRequesterId(userId).stream()
                .map(mapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId, PageRequest pageRequest) {
        return itemRequestRepository.findAllByRequesterIdNot(userId, pageRequest).stream()
                .map(mapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequest(Long requestId, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        ItemRequest request = itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запрос не найден"));
        ItemRequestDto itemRequestDto = mapper.toItemRequestDto(request);
        itemRequestDto.setItems(request.getItems().stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList()));
        return itemRequestDto;
    }
}
