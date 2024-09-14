package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.Instant;

@Data
public class Booking {
    Long id;
    Instant startTime;
    Instant endTime;
    Item item;
    User booker;

}
