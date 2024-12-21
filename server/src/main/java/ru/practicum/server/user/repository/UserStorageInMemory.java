package ru.practicum.server.user.repository;

import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.server.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Generated
@Slf4j
@Component
public class UserStorageInMemory implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private static final AtomicLong ID_SEQUENCE = new AtomicLong();

    public Collection<User> getAll() {
        Collection<User> values = users.values();
        log.debug("Получили всех users - {}", values);
        return users.values();
    }

    public User getById(Long id) {
        User getById = users.get(id);
        log.debug("Получили user по id - {}", getById);
        return getById;
    }

    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.debug("Создали user - {}", users.get(user.getId()));
        return users.get(user.getId());
    }

    public User update(User newUser, Long userId) {
        newUser.setId(userId);
        users.put(newUser.getId(), newUser);
        log.debug("Обновили user - {}", users.get(userId));
        return users.get(userId);
    }

    public void delete(Long id) {
        users.remove(id);
    }

    private Long getNextId() {
        long id = ID_SEQUENCE.incrementAndGet();
        log.info("Увеличили id - {}", id);
        return id;
    }
}
