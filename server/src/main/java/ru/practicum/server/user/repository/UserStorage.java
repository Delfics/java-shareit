package ru.practicum.server.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.server.user.model.User;

import java.util.Collection;

@Repository
public interface UserStorage {
    Collection<User> getAll();

    User getById(Long id);

    User create(User user);

    User update(User newUser, Long userId);

    void delete(Long id);
}
