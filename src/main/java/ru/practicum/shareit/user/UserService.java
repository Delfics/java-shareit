package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public interface UserService {
    Collection<User> getAll();

    User getById(Integer id);

    User create(User user);

    User update(User user, Integer userId);

    void delete(Integer id);

    User patch(User user, Integer userId);
}
