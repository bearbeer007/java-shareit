package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.DAO.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.ShortBookingItemDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.interfaces.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.DAO.UserRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.interfaces.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository; // заменили UserService на UserRepository
    private final BookingMapper bookingMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public BookingResponseDto createBookingByUser(BookingRequestDto bookingRequestDto, Long userId, Item item) {
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        var booking = bookingMapper.toBooking(bookingRequestDto);
        var startDate = booking.getStart();
        var endDate = booking.getEnd();
        booking.setBooker(user);
        booking.setItem(item);
        if (startDate.isAfter(endDate) || startDate.equals(endDate)) {
            throw new BadRequestException("Указано неправильное время начала и конца бронирования");
        }
        if (booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Владелец не может бронировать собственную вещь, userId = " + userId + " itemId = " + item.getId());
        }
        booking.setStatus(Status.WAITING);
        var createdBooking = bookingRepository.save(booking);
        return bookingMapper.toBookingResponseDto(createdBooking);
    }

    @Override
    @Transactional
    public BookingResponseDto managingBookingStatus(Long bookingId, Long userId, Boolean approved) {
        var booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование с таким id: " + bookingId + ", отсутствует."));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Указанный пользователь c userId = " + userId + " не является владельцем вещи c itemId = " + booking.getItem().getId());
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new BadRequestException("Бронирование уже было переведено из статуса WAITING");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        bookingRepository.save(booking);
        return bookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public BookingResponseDto getBookingById(Long userId, Long bookingId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        var booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование с таким id: " + bookingId + ", отсутствует."));
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return bookingMapper.toBookingResponseDto(booking);
        } else {
            throw new NotFoundException("Указанный пользователь c userId = " + userId + " не является автором бронирования или владельцем вещи c itemId = " + booking.getItem().getId());
        }
    }

    @Override
    public List<BookingResponseDto> getAllBookingsOfUser(Long userId, String state) {
        var validState = State.isStateValid(state);
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        switch (validState) {
            case ALL:
                var allBookings = bookingRepository.findAllBookingsByBookerIdOrderByStartDesc(userId);
                return bookingMapper.toListBookingResponseDto(allBookings);
            case CURRENT:
                var currentBookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
                return bookingMapper.toListBookingResponseDto(currentBookings);
            case PAST:
                var pastBookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                return bookingMapper.toListBookingResponseDto(pastBookings);
            case FUTURE:
                var futureBookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                return bookingMapper.toListBookingResponseDto(futureBookings);
            case WAITING:
                var waitingBookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                return bookingMapper.toListBookingResponseDto(waitingBookings);
            case REJECTED:
                var rejectedBookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                return bookingMapper.toListBookingResponseDto(rejectedBookings);
            default:
                throw new BadRequestException(String.format("Unknown state: %s", state));
        }
    }

    @Override
    public List<BookingResponseDto> getAllBookingsOfAllUserItems(Long userId, String state) {
        var validState = State.isStateValid(state);
        switch (validState) {
            case ALL:
                var allBookings = bookingRepository.findAllBookingsByItemOwnerIdOrderByStartDesc(userId);
                return bookingMapper.toListBookingResponseDto(allBookings);
            case CURRENT:
                var currentBookings = bookingRepository.findAllBookingsByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
                return bookingMapper.toListBookingResponseDto(currentBookings);
            case PAST:
                var pastBookings = bookingRepository.findAllBookingsByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                return bookingMapper.toListBookingResponseDto(pastBookings);
            case FUTURE:
                var futureBookings = bookingRepository.findAllBookingsByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                return bookingMapper.toListBookingResponseDto(futureBookings);
            case WAITING:
                var waitingBookings = bookingRepository.findAllBookingsByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                return bookingMapper.toListBookingResponseDto(waitingBookings);
            case REJECTED:
                var rejectedBookings = bookingRepository.findAllBookingsByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                return bookingMapper.toListBookingResponseDto(rejectedBookings);
            default:
                throw new BadRequestException(String.format("Unknown state: %s", state));
        }
    }

    @Override
    public Optional<ShortBookingItemDto> findLastBookingByItemId(Long itemId) {
        var bookings = bookingRepository.findLastBookingByItemId(itemId, LocalDateTime.now(), Status.REJECTED, Pageable.ofSize(1));
        var shortBookings = bookingMapper.toListShortBooking(bookings);
        return shortBookings.stream().findFirst();
    }

    @Override
    public Optional<ShortBookingItemDto> findFutureBookingByItemId(Long itemId) {
        var bookings = bookingRepository.findFutureBookingByItemId(itemId, LocalDateTime.now(), Status.REJECTED, Pageable.ofSize(1));
        var shortBookings = bookingMapper.toListShortBooking(bookings);
        return shortBookings.stream().findFirst();
    }

    @Override
    public Map<Item, List<Booking>> findAllBookingsByItemIds(List<Long> itemIds) {
        var bookingsFromDb = bookingRepository.findAllBookingsByItemIdInAndStatusNotOrderByStartDesc(itemIds, Status.REJECTED);
        return bookingsFromDb.stream()
                .collect(Collectors.groupingBy(Booking::getItem));
    }
}
