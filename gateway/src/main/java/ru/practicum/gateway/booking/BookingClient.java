package ru.practicum.gateway.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import ru.practicum.api.dto.BookingDto;
import ru.practicum.api.dto.State;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.utils.Utility;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createBooking(Long userId, BookingDto requestDto) {
        return post(Utility.EMPTY, userId, requestDto);
    }

    public ResponseEntity<Object> getBooking(Long userId, Long bookingId) {
        return get(Utility.SLASH + bookingId, userId);
    }

    public ResponseEntity<Object> getBookingByBookingId(Long bookingId, Long bookerId) {
        return get(Utility.SLASH + bookingId, bookerId);
    }

    public ResponseEntity<Object> getAllBookingsByBookerId(State state, Long bookerId) {
        Map<String, Object> parameters = Map.of("state", state.name());
        return get("?state={state}", bookerId, parameters);
    }

    public ResponseEntity<Object> findAllBookingsForAllItemsCurrentUser(State state, Long ownerId) {
        Map<String, Object> parameters = Map.of("state", state.name());
        return get("/owner?state={state}", ownerId, parameters);
    }

    public ResponseEntity<Object> patch(Long bookingId, Boolean approved, Long ownerId) {
        Map<String, Object> parameters = Map.of("approved", approved);
        return patch(Utility.SLASH + bookingId + "?approved={approved}", ownerId, parameters, null);
    }
}