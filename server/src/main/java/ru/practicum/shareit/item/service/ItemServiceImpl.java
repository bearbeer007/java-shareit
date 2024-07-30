package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingMapper;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto create(CreateItemRequest request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemMapper.fromCreateItemRequest(request);
        item.setOwner(user);
        Long requestId = request.getRequestId();
        if (requestId != null) {
            Optional<ItemRequest> itemRequest = itemRequestRepository.findById(requestId);

            if (itemRequest.isEmpty()) {
                throw new NotFoundException("Запрос не найден");
            }

            item.setRequest(itemRequest.get());
        } else {
            item.setRequest(null);
        }
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Необходим id пользователя");
        }
        Item newItem = itemMapper.toItem(itemDto);
        Item item = itemRepository.getReferenceById(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не имеет доступа к этой вещи");
        }

        if (newItem.getName() != null) {
            item.setName(newItem.getName());
        }
        if (newItem.getAvailable() != null) {
            item.setAvailable(newItem.getAvailable());
        }
        if (newItem.getDescription() != null) {
            item.setDescription(newItem.getDescription());
        }
        itemRepository.save(item);
        return getItemById(itemId, userId);
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty()) {
            throw new NotFoundException("Вещь не найдена");
        }
        ItemDto itemDto = itemMapper.toItemDto(itemOptional.get());

        itemDto.setLastBooking(getLastBooking(itemId, userId));
        itemDto.setNextBooking(getNextBooking(itemId, userId));
        itemDto.setComments(getComments(itemId));

        return itemDto;
    }

    @Override
    public List<ItemDto> getAllUserItems(Long id) {
        List<ItemDto> items = itemRepository.findAllByOwnerId(id).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
        for (ItemDto itemDto : items) {
            itemDto.setLastBooking(getLastBooking(itemDto.getId(), id));
            itemDto.setNextBooking(getNextBooking(itemDto.getId(), id));
            itemDto.setComments(getComments(itemDto.getId()));
        }
        return items;
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> result = itemRepository.search(text);
        return result.stream().map(itemMapper::toItemDto).collect(Collectors.toList());
    }


    @Override
    public CommentDto postComment(CommentDto commentDto, Long itemId, Long userId) {
        Comment comment = commentMapper.toComment(commentDto);
        comment.setAuthor(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден")));
        comment.setItem(itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена")));

        Long bookingsCount = bookingRepository.countAllByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now());
        if (bookingsCount == 0) {
            throw new IllegalArgumentException("Нельзя оставлять комментарии для вещи, которую не брали в аренду");
        }

        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);

        return commentMapper.toCommentDto(comment);
    }

    private BookingDto getLastBooking(Long itemId, Long userId) {
        List<Booking> pastBookings = bookingRepository.findPastBookings(itemId, userId, LocalDateTime.now());
        if (pastBookings.isEmpty()) {
            return null;
        }
        return bookingMapper.toBookingDto(pastBookings.get(0));
    }

    private BookingDto getNextBooking(Long itemId, Long userId) {
        List<Booking> nextBookings = bookingRepository.findFutureBookings(itemId, userId, LocalDateTime.now());
        if (nextBookings.isEmpty()) {
            return null;
        }
        return bookingMapper.toBookingDto(nextBookings.get(0));
    }

    private List<CommentDto> getComments(Long itemId) {
        return commentRepository.getAllByItemId(itemId).stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}