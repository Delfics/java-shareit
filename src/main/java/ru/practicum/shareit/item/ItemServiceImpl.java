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
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    public Collection<Item> getAll() {
        return itemStorage.getAll();
    }

    public Item getById(Long id) {
        if (itemStorage.getById(id) == null) {
            throw new NotFoundException("Item " + id + " не найден");
        }
        Item byId = itemStorage.getById(id);
        log.debug("Найден успешно item {}", byId.getId());
        return byId;
    }

    public Item create(Item item, Long userId) {
        if (userId == null) {
            throw new ValidationException("Необходимо указать id пользователя для " + item.getName());
        }
        item.setOwner(userService.getById(userId));
        if (item.getName().isEmpty() || item.getDescription().isEmpty() || item.getAvailable() == null) {
            throw new ValidationException("Необходимо указать Name или Description или Available для item " + item.getId());
        }
        item.setAvailable(item.getAvailable());
        Item itemCreated = itemStorage.create(item);
        log.debug("Создан успешно item {}", itemCreated.name);
        return itemCreated;
    }

    public Item update(Item newItem) {
        Item update = itemStorage.update(newItem);
        log.debug("Обновлён успешно item {}", update.name);
        return update;
    }

    public void delete(Long id) {
        Item byId = getById(id);
        itemStorage.delete(byId.getId());
        log.debug("Удалён успешно item {}", byId.getId());
    }

    public Item patch(Long itemId, Item item, Long userId) {
        if (getById(itemId) == null) {
            throw new NotFoundException(itemId + " не существует");
        } else if (!Objects.equals(getById(itemId).getOwner().getId(), userId)) {
            throw new NotFoundException(userId + " не владелец вещи " + itemId);
        }
        Item byId = getById(itemId);
        byId.setName(item.getName());
        byId.setDescription(item.getDescription());
        byId.setAvailable(item.getAvailable());
        Item update = update(byId);
        log.debug("Успшено изменено описание item {}", update.getId());
        return update;
    }

    public Collection<Item> getItemsOwner(Long ownerId) {
        User byId = userService.getById(ownerId);
        log.debug("Поиск предметов по id - {}", byId.getId());
        return itemStorage.getItemsOwner(byId.getId());
    }

    public Collection<Item> searchItems(String text) {
        log.debug("Поиск предметов по названию - {}", text);
        return itemStorage.searchItems(text);
    }
}
