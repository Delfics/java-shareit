package ru.practicum.server.booking.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.api.dto.BookingDto;
import ru.practicum.api.dto.ItemDto;
import ru.practicum.api.dto.Status;
import ru.practicum.api.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialization() throws IOException {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.of(2024, 12, 15, 14, 30, 0));
        bookingDto.setEnd(LocalDateTime.of(2024, 12, 15, 16, 30, 0));
        bookingDto.setItemId(123L);

        bookingDto.setItem(new ItemDto());
        bookingDto.setBooker(new UserDto());
        bookingDto.setStatus(Status.APPROVED);

        String json = objectMapper.writeValueAsString(bookingDto);

        assertThat(json).contains("\"start\":\"2024-12-15T14:30:00\"");
        assertThat(json).contains("\"end\":\"2024-12-15T16:30:00\"");

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"itemId\":123");

        assertThat(json).contains("\"status\":\"APPROVED\"");
    }

    @Test
    void testDeserialization() throws IOException {
        String json = "{\"id\":1,\"start\":\"2024-12-15T14:30:00\",\"end\":\"2024-12-15T16:30:00\",\"itemId\":123," +
                "\"item\":{},\"booker\":{},\"status\":\"APPROVED\"}";

        BookingDto bookingDto = objectMapper.readValue(json, BookingDto.class);

        assertThat(bookingDto.getId()).isEqualTo(1L);
        assertThat(bookingDto.getStart()).isEqualTo(LocalDateTime.of(2024, 12, 15, 14, 30, 0));
        assertThat(bookingDto.getEnd()).isEqualTo(LocalDateTime.of(2024, 12, 15, 16, 30, 0));
        assertThat(bookingDto.getItemId()).isEqualTo(123L);
        assertThat(bookingDto.getStatus()).isEqualTo(Status.APPROVED);

        assertThat(bookingDto.getItem()).isNotNull();
        assertThat(bookingDto.getBooker()).isNotNull();
    }
}
