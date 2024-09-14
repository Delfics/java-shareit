package ru.practicum.shareit.item;

import lombok.Data;
import ru.practicum.shareit.user.User;

@Data
public class Item {
    Long id;
    String name;
    String description;
    Boolean available;
    User owner;
}
