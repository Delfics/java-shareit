package ru.practicum.gateway.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.api.dto.BookingDto;
import ru.practicum.gateway.booking.BookingClient;
import ru.practicum.gateway.booking.BookingController;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {

    private final BookingClient bookingClient = Mockito.mock(BookingClient.class);

    @InjectMocks
    private BookingController bookingController;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
    }

    @Test
    void testGetBookingByBookingId() throws Exception {
        Long bookingId = 1L;
        Long bookerId = 2L;

        when(bookingClient.getBookingByBookingId(bookingId, bookerId)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", bookerId))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookingByBookingId(bookingId, bookerId);
    }

    @Test
    void testGetBookingByBookingId_NotFound() throws Exception {
        Long bookingId = 1L;
        Long bookerId = 2L;

        when(bookingClient.getBookingByBookingId(bookingId, bookerId)).thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", bookerId))
                .andExpect(status().isNotFound());

        verify(bookingClient, times(1)).getBookingByBookingId(bookingId, bookerId);
    }

    @Test
    void testGetAllBookingsByBookerId() throws Exception {
        Long bookerId = 2L;

        when(bookingClient.getAllBookingsByBookerId(any(), eq(bookerId)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .param("state", "ALL"))
                .andExpect(status().isOk());

        verify(bookingClient).getAllBookingsByBookerId(any(), eq(bookerId));
    }

    @Test
    void testFindAllBookingsForAllItemsCurrentUser() throws Exception {
        Long ownerId = 3L;

        when(bookingClient.findAllBookingsForAllItemsCurrentUser(any(), eq(ownerId)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", "ALL"))
                .andExpect(status().isOk());

        verify(bookingClient).findAllBookingsForAllItemsCurrentUser(any(), eq(ownerId));
    }

    @Test
    void testFindAllBookingsForAllItemsCurrentUser_NotFound() throws Exception {
        Long ownerId = 3L;

        when(bookingClient.findAllBookingsForAllItemsCurrentUser(any(), eq(ownerId)))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", "ALL"))
                .andExpect(status().isNotFound());

        verify(bookingClient).findAllBookingsForAllItemsCurrentUser(any(), eq(ownerId));
    }

    @Test
    void testCreateBooking_Valid() throws Exception {
        Long userId = 1L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));

        when(bookingClient.createBooking(eq(userId), any())).thenReturn(new ResponseEntity<>(HttpStatus.CREATED));

        mockMvc.perform(post("/bookings")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isCreated());

        verify(bookingClient, times(1)).createBooking(eq(userId), any());
    }

    @Test
    void testCreateBooking_NotFound() throws Exception {
        Long userId = 1L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now());

        when(bookingClient.createBooking(eq(userId), any())).thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(post("/bookings")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound());

        verify(bookingClient, times(1)).createBooking(eq(userId), any());
    }

    @Test
    void testPatchBooking() throws Exception {
        Long bookingId = 1L;
        Long ownerId = 2L;
        Boolean approved = true;

        when(bookingClient.patch(bookingId, approved, ownerId)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", ownerId)
                        .param("approved", approved.toString()))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).patch(bookingId, approved, ownerId);
    }
}
