package ru.practicum.server.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.server.user.model.User;

import java.util.Collection;

@Service
public interface UserService {
    Collection<User> getAll();

    User getById(Long id);

    User create(User user);

    User update(User user, Long userId);

    void deleteById(Long id);

    User patch(User user, Long userId);

    Boolean existsByEmail(String email);
}
