package ru.practicum.server.booking.mapper;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import ru.practicum.api.dto.BookingDto;
import ru.practicum.api.dto.ItemDto;
import ru.practicum.api.dto.UserDto;
import ru.practicum.api.dto.Status;
import ru.practicum.server.booking.mappers.BookingMapper;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.user.model.User;

import java.time.LocalDateTime;

public class BookingMapperTest {

    @Test
    void testToBooking() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.of(2024, 12, 15, 10, 0));
        bookingDto.setEnd(LocalDateTime.of(2024, 12, 15, 12, 0));
        bookingDto.setItemId(1L);

        Booking booking = BookingMapper.toBooking(bookingDto);

        assertThat(booking.getId()).isEqualTo(bookingDto.getId());
        assertThat(booking.getStart()).isEqualTo(bookingDto.getStart());
        assertThat(booking.getEnd()).isEqualTo(bookingDto.getEnd());
    }

    @Test
    void testToBookingDto() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2024, 12, 15, 10, 0));
        booking.setEnd(LocalDateTime.of(2024, 12, 15, 12, 0));

        Item item = new Item();
        item.setId(1L);
        item.setName("Item Name");
        booking.setItem(item);

        User user = new User();
        user.setId(2L);
        user.setName("John Doe");
        booking.setBooker(user);
        booking.setStatus(Status.APPROVED);

        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        assertThat(bookingDto).isNotNull();
        assertThat(bookingDto.getId()).isEqualTo(booking.getId());
        assertThat(bookingDto.getStart()).isEqualTo(booking.getStart());
        assertThat(bookingDto.getEnd()).isEqualTo(booking.getEnd());
        assertThat(bookingDto.getItem()).isNotNull();
        assertThat(bookingDto.getItem().getId()).isEqualTo(item.getId());
        assertThat(bookingDto.getItem().getName()).isEqualTo(item.getName());
        assertThat(bookingDto.getBooker()).isNotNull();
        assertThat(bookingDto.getBooker().getId()).isEqualTo(user.getId());
        assertThat(bookingDto.getStatus()).isEqualTo(booking.getStatus());
    }
}
