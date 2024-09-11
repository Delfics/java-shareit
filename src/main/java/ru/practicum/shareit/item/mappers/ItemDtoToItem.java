package ru.practicum.shareit.item.mappers;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;

public class ItemDtoToItem {
    public static Item toItem (ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }
}
