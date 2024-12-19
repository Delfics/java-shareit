package ru.practicum.gateway.booking;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.api.dto.BookingDto;
import ru.practicum.api.dto.State;
import ru.practicum.gateway.utils.HttpProperties;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingByBookingId(@PathVariable("bookingId") Long bookingId,
                                                        @RequestHeader(HttpProperties.xSharerUserId) Long bookerId) {
        log.info("Get - getBookingByBookingId. Входные параметры bookingId={}, bookerId={}",
                bookingId, bookerId);
        return bookingClient.getBookingByBookingId(bookingId, bookerId);

    }

    @GetMapping()
    public ResponseEntity<Object> getAllBookingsByBookerId(@RequestParam(required = false, defaultValue = "ALL") State state,
                                                           @RequestHeader(HttpProperties.xSharerUserId) Long bookerId) {
        log.info("Get - getAllBookingsByBookerId. Входные параметры bookerId={}", bookerId);
        return bookingClient.getAllBookingsByBookerId(state, bookerId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllBookingsForAllItemsCurrentUser(
            @RequestParam(required = false, defaultValue = "ALL") State state,
            @RequestHeader(HttpProperties.xSharerUserId) Long ownerId) {
        log.info("Get - findAllBookingsForAllItemsCurrentUser. Входные параметры ownerId={}", ownerId);
        return bookingClient.findAllBookingsForAllItemsCurrentUser(state, ownerId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createBooking(@RequestHeader(HttpProperties.xSharerUserId) Long userId,
                                                @Valid @RequestBody BookingDto bookingDto) {
        log.info("Post - createBooking. Входные параметры bookingDto={} , userId={} ",
                bookingDto.toString(), userId);
        return bookingClient.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> patch(@PathVariable Long bookingId, @RequestParam Boolean approved,
                                        @RequestHeader(HttpProperties.xSharerUserId) Long ownerId) {
        log.info("Patch - patch. Входные параметры bookingId ={} , userId={} ",
                bookingId, ownerId);
        return bookingClient.patch(bookingId, approved, ownerId);
    }
}