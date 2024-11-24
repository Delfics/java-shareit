package ru.practicum.server.itemrequest.model;

import lombok.Data;
import ru.practicum.server.item.model.Item;

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
