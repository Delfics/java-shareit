package ru.practicum.server.item.service;

import lombok.Generated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repository.ItemStorage;
import ru.practicum.server.item.validator.Valid;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Generated
@Service
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

    public Item createItem(Item item, Long userId) {
        Valid.throwExIfUserIdNull(userId, item);
        item.setOwner(userService.getById(userId));
        Valid.throwExIfNameOrDescriptionAreEmptyAndAvailableIsNull(item);
        item.setAvailable(item.getAvailable());
        Item itemCreated = itemStorage.create(item);
        log.debug("Создан успешно item {}", itemCreated.getName());
        return itemCreated;
    }

    public Item update(Item newItem) {
        Item update = itemStorage.update(newItem);
        log.debug("Обновлён успешно item {}", update.getName());
        return update;
    }

    public void deleteById(Long id) {
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

    public List<Item> findItemsByOwnerId(Long ownerId) {
        User byId = userService.getById(ownerId);
        log.debug("Поиск предметов по id - {}", byId.getId());
        return itemStorage.getItemsOwner(byId.getId());
    }

    public Collection<Item> findItemsByText(String text) {
        log.debug("Поиск предметов по названию - {}", text);
        return itemStorage.searchItems(text);
    }
}
