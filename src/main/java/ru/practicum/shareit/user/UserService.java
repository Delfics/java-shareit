package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public interface UserService {
    Collection<User> getAll();

    User getById(Long id);

    User create(User user);

    User update(User user, Long userId);

    void delete(Long id);

    User patch(User user, Long userId);
}
