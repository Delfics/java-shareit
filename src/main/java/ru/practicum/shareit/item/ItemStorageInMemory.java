package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class ItemStorageInMemory implements ItemStorage {
    private final Map<Integer, Item> items = new HashMap<>();

    public Collection<Item> getAll() {
        return items.values();
    }

    public Item getById(Integer id) {
        return items.get(id);
    }

    public Item create(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    public Item update(Item newItem) {
        return items.put(newItem.getId(), newItem);
    }

    public void delete(Integer id) {
        items.remove(id);
    }

    private Integer getNextId() {
        int currentMaxId = items.keySet()
                .stream()
                .max(Integer::compareTo)
                .orElse(0);
        return ++currentMaxId;
    }

}
