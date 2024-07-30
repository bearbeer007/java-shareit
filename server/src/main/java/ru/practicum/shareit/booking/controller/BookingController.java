package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;


import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService service;

    @PostMapping
    public ResponseEntity<BookingDto> requestBooking(@RequestBody @Valid BookingDto bookingDto,
                                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Post-запрос на добавление нового запроса на бронирование");
        return ResponseEntity.ok().body(service.requestBooking(bookingDto, userId));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approveBooking(@PathVariable Long bookingId,
                                                     @RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam Boolean approved) {
        log.info("Patch-запрос на подтверждение или отклонение запроса на бронирование");
        return ResponseEntity.ok().body(service.approveBooking(bookingId, userId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBooking(@PathVariable Long bookingId,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get-запрос на получении данных о бронировании id={} от пользователя userId={}", bookingId, userId);
        return ResponseEntity.ok().body(service.getBooking(bookingId, userId));
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getAllBookingsByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                 @RequestParam(defaultValue = "ALL") String state) {
        log.info("Get-запрос на получение списка всех бронирований текущего пользователя id={}", userId);
        return ResponseEntity.ok().body(service.getAllBookingsByUser(userId, state));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                               @RequestParam(defaultValue = "ALL") String state) {
        log.info("Get-запрос на получение списка бронирований для всех вещей текущего пользователя id={}", userId);
        return ResponseEntity.ok().body(service.getBookingsByOwner(userId, state));
    }
}