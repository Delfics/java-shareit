package ru.practicum.shareit.booking.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.mappers.UserMapper;

@UtilityClass
public class BookingMapper {
    public Booking toBooking(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(ItemMapper.toItem(bookingDto.getItem()));
        booking.setBooker(UserMapper.toUser(bookingDto.getBooker()));
        booking.setStatus(bookingDto.getStatus());
        return booking;
    }

    public BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());

        ItemDto itemDto = new ItemDto();
        itemDto.setId(booking.getItem().getId());
        itemDto.setName(booking.getItem().getName());
        bookingDto.setItem(itemDto);

        UserDto userDto = new UserDto();
        userDto.setId(booking.getBooker().getId());
        bookingDto.setBooker(userDto);
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }
}
