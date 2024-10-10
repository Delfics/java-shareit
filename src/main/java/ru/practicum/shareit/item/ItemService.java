package ru.practicum.shareit.item;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    Collection<Item> getAll();

    Item getById(Long id);

    Item createItem(Item item, Long userId);

    Item update(Item newItem);

    void deleteById(Long id);

    Item patch(Long itemId, Item item, Long userId);

    List<Item> findItemsByOwnerId(Long id);

}
