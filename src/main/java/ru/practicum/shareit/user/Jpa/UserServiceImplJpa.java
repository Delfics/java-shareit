package ru.practicum.shareit.user.Jpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Optional;

@Primary
@Service
@Slf4j
public class UserServiceImplJpa implements UserService {
    private final UserStorageJpa userStorageJpa;

    @Autowired
    public UserServiceImplJpa(UserStorageJpa userStorageJpa) {
        this.userStorageJpa = userStorageJpa;
    }

    public List<User> getAll() {
        return userStorageJpa.findAll();
    }

    public User create(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            throw new ValidationException("Email указан неправильно");
        }
        if (existsByEmail(user.getEmail())) {
            throw new ConflictException("Такой email уже существует");
        }
        userStorageJpa.save(user);
        Optional<User> byId = userStorageJpa.findById(user.getId());
        log.debug("Создали user - {} ", byId.get().getEmail());
        return byId.get();
    }

    public User getById(Long id) {
        Optional<User> byId = userStorageJpa.findById(id);
        if (byId.isPresent()) {
            return byId.get();
        } else {
            throw new NotFoundException("Пользователь с таким id не найден " + id);
        }
    }

    public User patch(User user, Long userId) {
        if (user.getName() == null) {
            if (getById(userId) == null) {
                throw new ValidationException("Поля name не должно быть пустым");
            }
            User byId = getById(userId);
            user.setName(byId.getName());
        }
        if (existsByEmail(user.getEmail())) {
            throw new ConflictException("Такой email уже существует");
        }
        if (getById(userId).getEmail() == null && user.getEmail() == null) {
            throw new ValidationException("Поля email при изменении не должно быть пустым");
        }
        User update = update(user, userId);
        log.debug("Пользователь {} изменён", update.getId());
        return update;
    }

    public User update(User user, Long userId) {
        Optional<User> byId = userStorageJpa.findById(userId);
        if (byId.isPresent()) {
            if (!byId.get().getName().equals(user.getName()) && user.getName() != null) {
                byId.get().setName(user.getName());
            }
            if (!byId.get().getEmail().equals(user.getEmail()) && user.getEmail() != null) {
                byId.get().setEmail(user.getEmail());
            }
            userStorageJpa.save(byId.get());
        } else {
            throw new NotFoundException("Пользователь с таким id не найден " + user.getId());
        }
        return userStorageJpa.findById(byId.get().getId()).get();
    }

    public void deleteById(Long userId) {
        if (userStorageJpa.findById(userId).isPresent()) {
            userStorageJpa.deleteById(userId);
            log.debug("Пользователь {} удалён", userId);
        } else {
            throw new NotFoundException("Пользователь с таким id не найден " + userId);
        }
    }

    public Boolean existsByEmail(String email) {
        return userStorageJpa.existsByEmail(email);
    }

}
