package ru.practicum.server.booking.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.api.dto.BookingDto;
import ru.practicum.api.dto.ItemDto;
import ru.practicum.api.dto.Status;
import ru.practicum.api.dto.UserDto;
import ru.practicum.server.booking.mappers.BookingMapper;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingMapperTest {

    @Test
    void testToBooking_WithItem() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Item Name");
        itemDto.setDescription("Item Description");
        itemDto.setAvailable(true);

        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setName("John Doe");
        userDto.setEmail("john.doe@example.com");

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.of(2024, 12, 15, 10, 0));
        bookingDto.setEnd(LocalDateTime.of(2024, 12, 15, 12, 0));
        bookingDto.setItemId(1L);
        bookingDto.setItem(itemDto);
        bookingDto.setBooker(userDto);
        bookingDto.setStatus(Status.APPROVED);

        Booking booking = BookingMapper.toBooking(bookingDto);

        assertThat(booking.getId()).isEqualTo(bookingDto.getId());
        assertThat(booking.getStart()).isEqualTo(bookingDto.getStart());
        assertThat(booking.getEnd()).isEqualTo(bookingDto.getEnd());
        assertThat(booking.getItem().getId()).isEqualTo(bookingDto.getItem().getId());
        assertThat(booking.getItem().getName()).isEqualTo(bookingDto.getItem().getName());
        assertThat(booking.getItem().getDescription()).isEqualTo(bookingDto.getItem().getDescription());
        assertThat(booking.getItem().getAvailable()).isEqualTo(bookingDto.getItem().getAvailable());
        assertThat(booking.getBooker().getId()).isEqualTo(bookingDto.getBooker().getId());
        assertThat(booking.getBooker().getName()).isEqualTo(bookingDto.getBooker().getName());
        assertThat(booking.getBooker().getEmail()).isEqualTo(bookingDto.getBooker().getEmail());
    }

    @Test
    void testToBooking_WhenItemIdNotNullAndItemAreNull() {
        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setName("John Doe");
        userDto.setEmail("john.doe@example.com");

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.of(2024, 12, 15, 10, 0));
        bookingDto.setEnd(LocalDateTime.of(2024, 12, 15, 12, 0));
        bookingDto.setItemId(1L);
        bookingDto.setBooker(userDto);
        bookingDto.setStatus(Status.APPROVED);

        Booking booking = BookingMapper.toBooking(bookingDto);

        assertThat(booking.getId()).isEqualTo(bookingDto.getId());
        assertThat(booking.getStart()).isEqualTo(bookingDto.getStart());
        assertThat(booking.getEnd()).isEqualTo(bookingDto.getEnd());
        assertThat(booking.getItem()).isNotNull();
        assertThat(booking.getBooker().getId()).isEqualTo(bookingDto.getBooker().getId());
        assertThat(booking.getBooker().getName()).isEqualTo(bookingDto.getBooker().getName());
        assertThat(booking.getBooker().getEmail()).isEqualTo(bookingDto.getBooker().getEmail());
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
