package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.interfaces.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.DAO.ItemRepository;
import ru.practicum.shareit.item.comment.DAO.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.interfaces.ItemService;
import ru.practicum.shareit.user.DAO.UserRepository;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final BookingService bookingService;

    @Override
    @Transactional
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        var user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id: " + userId + " отсутствует."));
        var item = itemMapper.toItem(itemDto);
        item.setOwner(user);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id: " + userId + " отсутствует."));
        var item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Вещь с таким id: " + itemId + ", отсутствует."));
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Данный пользователь " + userId + " не является владельцем вещи с id: " + itemId);
        }
        itemMapper.updateItemFromItemDto(itemDto, item);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItemById(Long userId, Long itemId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id: " + userId + " отсутствует."));
        var item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Вещь с таким id: " + itemId + ", отсутствует."));
        var itemDto = itemMapper.toItemDto(item);
        if (item.getOwner().getId().equals(userId)) {
            var lastBooking = bookingService.findLastBookingByItemId(item.getId());
            var nextBooking = bookingService.findFutureBookingByItemId(item.getId());
            lastBooking.ifPresent(itemDto::setLastBooking);
            nextBooking.ifPresent(itemDto::setNextBooking);
        }
        var commentsDto = commentMapper.toListCommentsDto(commentRepository.findAllByItemId(itemId));
        itemDto.setComments(commentsDto);
        return itemDto;
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id: " + userId + " отсутствует."));
        var allItemsDto = itemRepository.findAllByOwnerIdOrderById(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
        var itemIds = allItemsDto.stream().map(ItemDto::getId).collect(Collectors.toList());
        var bookingsMap = bookingService.findAllBookingsByItemIds(itemIds);
        var comments = commentRepository.findAllCommentsByItemIdInOrderByCreatedDesc(itemIds);
        var commentsMap = comments.stream().collect(Collectors.groupingBy(Comment::getItem));

        Map<Long, List<Optional<Booking>>> resultBookingsMap = new HashMap<>();
        bookingsMap.forEach((item, bookings) -> {
            Optional<Booking> lastBooking = bookings.stream()
                    .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                    .min(Comparator.comparing(Booking::getEnd));

            Optional<Booking> nextBooking = bookings.stream()
                    .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                    .min(Comparator.comparing(Booking::getStart));

            resultBookingsMap.put(item.getId(), List.of(lastBooking, nextBooking));
        });

        for (ItemDto itemDto : allItemsDto) {
            var itemBookings = resultBookingsMap.get(itemDto.getId());
            if (itemBookings != null) {
                var last = itemBookings.get(0);
                var next = itemBookings.get(1);
                last.ifPresent(booking -> itemDto.setLastBooking(bookingMapper.toShortBooking(booking)));
                next.ifPresent(booking -> itemDto.setNextBooking(bookingMapper.toShortBooking(booking)));
            }
            var itemComments = commentsMap.get(itemMapper.toItem(itemDto));
            itemDto.setComments(commentMapper.toListCommentsDto(itemComments));
        }
        return allItemsDto;
    }

    @Override
    public List<ItemDto> searchItemToRent(Long userId, String text) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id: " + userId + " отсутствует."));
        if (text == null) {
            return Collections.emptyList();
        }
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchItemToRent(text).stream()
                .filter(Item::getAvailable)
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) {
        var user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id: " + userId + " отсутствует."));
        var comment = commentMapper.toComment(commentDto);

        var item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Вещь с таким id: " + itemId + ", отсутствует."));
        if (item.getOwner().getId().equals(userId)) {
            throw new BadRequestException("Данный пользователь " + userId + " является владельцем вещи с id: " + itemId);
        }
        comment.setItem(item);
        var pastBookings = bookingService.getAllBookingsOfUser(userId, String.valueOf(State.PAST));
        if (pastBookings.isEmpty()) {
            throw new BadRequestException("Пользователь c userId" + userId + " не брал вещь в аренду c itemId " + itemId);
        }
        for (BookingResponseDto pastBooking : pastBookings) {
            if (pastBooking.getItem().getId().equals(itemId) && pastBooking.getBooker().getId().equals(userId) && pastBooking.getStatus().equals(Status.APPROVED)) {
                comment.setAuthor(user);
                commentRepository.save(comment);
            } else {
                throw new BadRequestException("Пользователь c userId" + userId + " не брал вещь в аренду c itemId " + itemId);
            }
        }
        return commentMapper.toCommentDto(comment);
    }

    @Override
    public void isUserHaveItems(Long userId) {
        var userItems = getUserItems(userId);
        if (userItems.isEmpty()) {
            throw new BadRequestException("У данного пользователя нет вещей, userId - " + userId);
        }
    }

    @Override
    public Item isItemAvailable(Long itemId) {
        var item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Вещь с таким id: " + itemId + ", отсутствует."));
        if (!item.getAvailable()) {
            throw new BadRequestException("Данная вещь не доступна к бронированию, itemId - " + itemId);
        }
        return item;
    }
}
