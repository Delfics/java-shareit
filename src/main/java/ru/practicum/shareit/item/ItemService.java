package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.Collection;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    public Collection<Item> getAll() {
        log.info("Получение всех items");
        return itemStorage.getAll();
    }

    public Item getById(Integer id) {
        if (itemStorage.getById(id) == null) {
            throw new NotFoundException("Item " + id + " не найден");
        }
        log.info("Найден успешно item {}", id);
        return itemStorage.getById(id);
    }

    public Item create(Item item, String userId) {
        if (userId == null || isNotNumber(userId)) {
            throw new ValidationException("Необходимо указать id пользователя для " + item.getName());
        }
        item.setOwner(userService.getById(Integer.parseInt(userId)));
        if (item.getName().isEmpty() || item.getDescription().isEmpty() || item.getAvailable() == null) {
            throw new ValidationException("Необходимо указать Name или Description или Available для item " + item.getId());
        }
        item.setAvailable(item.getAvailable());
        log.info("Создан успешно item {}", item.name);
        return itemStorage.create(item);
    }

    public Item update(Item newItem) {
        return itemStorage.update(newItem);
    }

    public void delete(Integer id) {
        itemStorage.delete(id);
    }

    public Item patch(Integer itemId, Item item, String userId) {
        if (getById(itemId) == null) {
            throw new NotFoundException(itemId + " не существует");
        } else if (getById(itemId).getOwner().getId() != Integer.parseInt(userId)) {
            throw new NotFoundException(userId + " не владелец вещи " + itemId);
        }
        if (item.getName() != null || item.getDescription() != null || item.getAvailable() != null) {
            Item byId = getById(itemId);
            byId.setName(item.getName());
            byId.setDescription(item.getDescription());
            byId.setAvailable(item.getAvailable());
            log.info("Успшено изменено описание item {}", byId.getId());
            return update(byId);
        } else {
            throw new ValidationException("Name, Description, Status - изменяемые поля не могут быть пустыми");
        }
    }

    public Collection<Item> getItemsOwner(String ownerId) {
        User byId = userService.getById(Integer.parseInt(ownerId));
        log.info("Поиск предметов по id - {}", ownerId);
        return getAll().stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), byId.getId()))
                .toList();
    }

    public Collection<Item> searchItems(String text) {
        final String textLowerCase = text.toLowerCase();
        Collection<Item> all = getAll();
        log.info("Поиск предметов по названию - {}", text);
        return all.stream()
                .filter(item -> ((item.getName() != null && textLowerCase.contains(item.getName().toLowerCase())) ||
                        (item.getDescription() != null && textLowerCase.contains(item.getDescription().toLowerCase()))) &&
                        (item.getAvailable() != null && item.getAvailable().equals(true)))
                .toList();
    }

    private boolean isNotNumber(String str) {
        try {
            Double.parseDouble(str);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }
}
