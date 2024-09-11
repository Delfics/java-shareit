package ru.practicum.shareit.item.mappers;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;

public class ItemToItemDto {
    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        return itemDto;
    }
}
