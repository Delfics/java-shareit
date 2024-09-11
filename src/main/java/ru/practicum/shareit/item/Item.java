package ru.practicum.shareit.item;

import lombok.Data;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    Integer id;
    String name;
    String description;
    Boolean available;
    User owner;
}
