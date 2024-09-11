package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ItemStorage {

    Collection<Item> getAll();

    Item getById(Integer id);

    Item create(Item item);

    Item update(Item newItem);

    void delete(Integer id);
}
