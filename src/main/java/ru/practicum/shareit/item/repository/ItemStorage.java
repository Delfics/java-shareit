package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

@Repository
public interface ItemStorage {

    Collection<Item> getAll();

    Item getById(Long id);

    Item create(Item item);

    Item update(Item newItem);

    void delete(Long id);

    Collection<Item> searchItems(String text);

    List<Item> getItemsOwner(Long ownerId);
}
