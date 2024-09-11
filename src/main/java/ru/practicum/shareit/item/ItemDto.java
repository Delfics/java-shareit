package ru.practicum.shareit.item;

import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class ItemDto {
    Integer id;
    String name;
    String description;
    Boolean available;
}
