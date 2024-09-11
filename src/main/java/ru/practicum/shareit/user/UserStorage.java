package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface UserStorage {
    Collection<User> getAll();

    User getById(Integer id);

    User create(User user);

    User update(User newUser, Integer userId);

    void delete(Integer id);
}
