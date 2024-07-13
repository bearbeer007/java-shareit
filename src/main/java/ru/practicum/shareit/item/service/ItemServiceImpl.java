package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toUnmodifiableList;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Long create(ItemCreateDto item, long ownerId) {
        User user = userRepository.findById(ownerId).orElseThrow(() -> new UserNotFoundException(ownerId));
        final Item itemEntity = ItemMapper.toItem(item, user);
        return itemRepository.add(itemEntity, user.getId());
    }

    @Override
    public ItemDto createAndGet(ItemCreateDto item, long ownerId) {
        User user = userRepository.findById(ownerId).orElseThrow(() -> new UserNotFoundException(ownerId));
        final Item itemEntity = ItemMapper.toItem(item, user);
        final Long itemId = itemRepository.add(itemEntity, user.getId());
        return ItemMapper.toItemDto(itemEntity);
    }

    @Override
    public ItemDto update(long id, ItemDto item, long ownerId) {
        final Optional<Item> itemOpt = itemRepository.getByIdAndOwnerId(id, ownerId);
        final Item itemFromRepo = itemOpt.orElseThrow(() -> new ItemNotFoundException(id));

        final Item changedItem = ItemMapper.updateIfDifferent(itemFromRepo, item);

        Item updatedItem;
        if (itemFromRepo.equals(changedItem)) {
            updatedItem = changedItem;
        } else {
            updatedItem = itemRepository.update(changedItem, ownerId);
        }

        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getById(long id) {
        final Optional<Item> itemOpt = itemRepository.getById(id);
        final Item item = itemOpt.orElseThrow(() -> new ItemNotFoundException(id));

        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getOwnerItemById(long itemId, long ownerId) {
        final Optional<Item> itemOpt = itemRepository.getByIdAndOwnerId(itemId, ownerId);
        final Item item = itemOpt.orElseThrow(() -> new ItemNotFoundException(itemId));

        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllOwnerItems(long ownerId) {
        final List<Item> ownerItems = itemRepository.getOwnerItems(ownerId);
        return ownerItems.stream().map(ItemMapper::toItemDto).collect(toUnmodifiableList());
    }

    @Override
    public List<ItemDto> searchItems(String text, long userId) {
        final String searchText = text.trim().toLowerCase();

        List<Item> searchResult = new ArrayList<>();
        if (!searchText.isEmpty()) {
            searchResult = itemRepository.search(searchText, userId);
        }

        return searchResult.stream().map(ItemMapper::toItemDto).collect(toUnmodifiableList());
    }
}