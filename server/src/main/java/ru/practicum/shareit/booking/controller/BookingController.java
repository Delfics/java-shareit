package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mappers.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.myenums.State;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.utils.HttpProperties;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    private final BookingServiceImpl bookingService;

    @Autowired
    BookingController(BookingServiceImpl bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingByBookingId(@PathVariable("bookingId") Long bookingId,
                                            @RequestHeader(HttpProperties.xSharerUserId) Long bookerId) {
        log.info("Запрос Get - getBookingByBookingId. Входные параметры bookingId {}, bookerId - {}",
                bookingId, bookerId);
        return BookingMapper.toBookingDto(bookingService.findBookingByBookingIdAndUserId(bookingId, bookerId));

    }

    @GetMapping()
    public List<BookingDto> getAllBookingsByBookerId(@RequestParam(required = false, defaultValue = "ALL") State state,
                                                     @RequestHeader(HttpProperties.xSharerUserId) Long bookerId) {
        log.info("Запрос Get - getAllBookingsByBookerId. Входные параметры bookerId - {}", bookerId);
        List<Booking> allBookingsByBookerId = bookingService.findAllBookingsByBookerId(state, bookerId);
        return allBookingsByBookerId.stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllBookingsForAllItemsCurrentUser(
            @RequestParam(required = false, defaultValue = "ALL") State state,
            @RequestHeader(HttpProperties.xSharerUserId) Long ownerId) {
        log.info("Запрос Get - findAllBookingsForAllItemsCurrentUser. Входные параметры ownerId - {}", ownerId);
        List<Booking> allBookingsWithAllItemsByOwnerId = bookingService.findAllBookingsWithAllItemsByOwnerId(state, ownerId);
        return allBookingsWithAllItemsByOwnerId.stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto create(@Valid @RequestBody BookingDto bookingDto,
                             @RequestHeader(HttpProperties.xSharerUserId) Long userId) {
        log.info("Запрос Post - create. Входные параметры bookingDto - {} , userId {} ",
                bookingDto.toString(), userId);
        return BookingMapper.toBookingDto(bookingService.create(BookingMapper.toBooking(bookingDto), userId));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto patch(@PathVariable Long bookingId, @RequestParam Boolean approved,
                            @RequestHeader(HttpProperties.xSharerUserId) Long ownerId) {
        log.info("Запрос Patch - create. Входные параметры bookingId - {} , userId {} ",
                bookingId, ownerId);
        return BookingMapper.toBookingDto(bookingService.patch(bookingId, approved, ownerId));
    }
}
