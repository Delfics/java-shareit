package ru.practicum.gateway.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import ru.practicum.api.dto.BookingDto;
import ru.practicum.api.dto.State;
import ru.practicum.gateway.Application;
import ru.practicum.gateway.booking.BookingController;
import ru.practicum.gateway.config.TestConfiguration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {Application.class, TestConfiguration.class})
@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private BookingController bookingController;
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    State state = State.ALL;

    Map<String, Object> parameters = Map.of("state", state.name());

    private final DateTimeFormatter formatJson = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
    }

    @Test
    void testGetBookingByBookingId() throws Exception {
        Long bookingId = 1L;
        Long bookerId = 2L;

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class))).thenReturn(ResponseEntity.status(HttpStatus.OK).build());

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", bookerId))
                .andExpect(status().isOk());
    }

    @Test
    void testGetBookingByBookingId_NotFound() throws Exception {
        Long bookingId = 1L;
        Long bookerId = 2L;

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class))).thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", bookerId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllBookingsByBookerId() throws Exception {
        Long bookerId = 2L;

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class), eq(parameters))).thenReturn(ResponseEntity.status(HttpStatus.OK).build());

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .param("state", "ALL"))
                .andExpect(status().isOk());

    }

    @Test
    void testFindAllBookingsForAllItemsCurrentUser() throws Exception {
        Long ownerId = 3L;

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class), eq(parameters))).thenReturn(ResponseEntity.status(HttpStatus.OK).build());

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", "ALL"))
                .andExpect(status().isOk());
    }

    @Test
    void testFindAllBookingsForAllItemsCurrentUser_NotFound() throws Exception {
        Long ownerId = 3L;

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class), eq(parameters))).thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", "ALL"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateBooking_Valid() throws Exception {
        Long userId = 1L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));
        String start = bookingDto.getStart().format(formatJson);
        String end = bookingDto.getEnd().format(formatJson);

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class))).thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(bookingDto));

        mockMvc.perform(post("/bookings")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(start)))
                .andExpect(jsonPath("$.end", is(end)));
    }

    @Test
    void testCreateBooking_NotFound() throws Exception {
        Long userId = 1L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now());

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class))).thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        mockMvc.perform(post("/bookings")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testPatchBooking() throws Exception {
        Long bookingId = 1L;
        Long ownerId = 2L;
        Boolean approved = true;
        Map<String, Object> parameters = Map.of("approved", approved);

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class), eq(parameters))).thenReturn(ResponseEntity.status(HttpStatus.OK).build());

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", ownerId)
                        .param("approved", approved.toString()))
                .andExpect(status().isOk());
    }
}
