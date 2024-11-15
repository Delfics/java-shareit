package ru.practicum.shareit.itemrequest.model;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Data
public class ItemRequestWithItems {
    ItemRequest itemRequest;
    List<Item> items;

    public ItemRequestWithItems(ItemRequest itemRequest, List<Item> items) {
        this.itemRequest = itemRequest;
        this.items = items;
    }
}
