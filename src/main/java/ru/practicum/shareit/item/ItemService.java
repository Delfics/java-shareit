package ru.practicum.shareit.item;

import java.util.Collection;

public interface ItemService {
    Collection<Item> getAll();

    Item getById(Long id);

    Item create(Item item, Long userId);

    Item update(Item newItem);

    void delete(Long id);

    Item patch(Long itemId, Item item, Long userId);

    Collection<Item> getItemsOwner(Long ownerId);

    Collection<Item> searchItems(String text);
}
