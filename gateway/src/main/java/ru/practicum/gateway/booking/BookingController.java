package ru.practicum.gateway.booking;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.api.BookingDto;
import ru.practicum.api.State;
import ru.practicum.gateway.utils.HttpProperties;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    /*@GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        State state = State.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }*/

    /*@PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid BookingDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.createBooking(userId, requestDto);
    }
*/
    /*@GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }*/

    /*@GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }*/

    //выше удалить

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
        return bookingClient.findAllBookingsForAllItemsCurrentUser(state , ownerId);
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