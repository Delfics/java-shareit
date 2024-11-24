package ru.practicum.server.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.api.BookingDto;
import ru.practicum.api.ItemDto;
import ru.practicum.api.UserDto;
import ru.practicum.server.item.mappers.ItemMapper;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.user.mappers.UserMapper;


@UtilityClass
public class BookingMapper {
    public Booking toBooking(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        if (bookingDto.getItem() != null) {
            booking.setItem(ItemMapper.toItem(bookingDto.getItem()));
        } else if (bookingDto.getItemId() != null) {
            Item item = new Item();
            item.setId(bookingDto.getItemId());
            booking.setItem(item);
        } else if (bookingDto.getBooker() != null) {
            booking.setBooker(UserMapper.toUser(bookingDto.getBooker()));
        } else if (bookingDto.getStatus() != null) {
            booking.setStatus(bookingDto.getStatus());
        }
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
