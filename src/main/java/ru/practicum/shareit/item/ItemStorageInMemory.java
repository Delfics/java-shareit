package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class ItemStorageInMemory implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private static final AtomicLong ID_SEQUENCE = new AtomicLong();

    public Collection<Item> getAll() {
        Collection<Item> values = items.values();
        log.info("Получили все items {}", values);
        return values;
    }

    public Item getById(Long id) {
        Item item = items.get(id);
        log.debug("Получили по id item - {}", item);
        return item;
    }

    public Item create(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        log.debug("Создали item - {}", items.get(item.getId()));
        return items.get(item.getId());
    }

    public Item update(Item newItem) {
        items.put(newItem.getId(), newItem);
        log.debug("Обновили item - {}", items.get(newItem.getId()));
        return items.get(newItem.getId());
    }

    public void delete(Long id) {
        items.remove(id);
    }

    public Collection<Item> getItemsOwner(Long ownerId) {
        List<Item> itemsOwner = getAll().stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), ownerId))
                .toList();
        log.debug("Получение предметов - {}, пользователя - {}", itemsOwner, ownerId);
        return itemsOwner;
    }

    public Collection<Item> searchItems(String text) {
        final String textLowerCase = text.toLowerCase();
        Collection<Item> all = getAll();
        List<Item> foundItems = all.stream()
                .filter(item -> ((item.getName() != null && textLowerCase.contains(item.getName().toLowerCase()) ||
                        (item.getDescription() != null && textLowerCase.contains(item.getDescription().toLowerCase()))) &&
                        (item.getAvailable().equals(true))))
                .toList();
        log.debug("Найденные предметы - {}", foundItems);
        return foundItems;
    }

    private Long getNextId() {
        long id = ID_SEQUENCE.incrementAndGet();
        log.debug("Увеличили id - {}", id);
        return id;
    }
}
