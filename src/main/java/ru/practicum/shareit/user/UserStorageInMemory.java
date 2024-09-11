package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserStorageInMemory implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    public Collection<User> getAll() {
        return users.values();
    }

    public User getById(Integer id) {
        return users.get(id);
    }

    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    public User update(User newUser, Integer userId) {
        newUser.setId(userId);
        users.put(newUser.getId(), newUser);
        return users.get(newUser.getId());
    }

    public void delete(Integer id) {
        users.remove(id);
    }

    private Integer getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .max(Integer::compareTo)
                .orElse(0);
        return ++currentMaxId;
    }
}
