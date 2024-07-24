package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.DAO.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.interfaces.ItemService;

@Service
@RequiredArgsConstructor
public class ItemBookingValidationService {

    private final ItemService itemService;
    private final ItemRepository itemRepository;

    public void isUserHaveItems(Long userId) {
        var userItems = itemService.getUserItems(userId);
        if (userItems.isEmpty()) {
            throw new BadRequestException("У данного пользователя нет вещей, userId - " + userId);
        }
    }

    public Item isItemAvailable(Long itemId) {
        var item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Вещь с таким id: " + itemId + ", отсутствует."));
        if (!item.getAvailable()) {
            throw new BadRequestException("Данная вещь не доступна к бронированию, itemId - " + itemId);
        }
        return item;
    }
}
