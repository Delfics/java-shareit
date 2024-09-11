package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    public Collection<User> getAll() {
        log.info("Получение всех Users");
        return userStorage.getAll();
    }

    public User getById(Integer id) {
        if (userStorage.getById(id) == null) {
            throw new NotFoundException("User " + id + " не найден");
        }
        log.info("Получение User по id - {}", id);
        return userStorage.getById(id);
    }

    public User create(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            throw new ValidationException("Email указан неправильно");
        }
        for (User userExist : getAll()) {
            validateEmail(userExist, user);
        }
        log.info("Создали user - {} ", user.getEmail());
        return userStorage.create(user);
    }

    public User patch(User user, Integer userId) {
        if (userStorage.getById(userId) == null) {
            throw new NotFoundException("Такого " + userId + " не сущесттвует");
        } else if (user.getName() == null) {
            if (getById(userId) == null) {
                throw new ValidationException("Поля name не должно быть пустым");
            }
            User byId = getById(userId);
            user.setName(byId.getName());
        }
        if (getById(userId).getEmail() == null && user.getEmail() == null) {
            throw new ValidationException("Поля email при изменении не должно быть пустым");
        }
        for (User userExist : getAll()) {
            if (getById(userId).getEmail() == null || user.getEmail() == null) {
                continue;
            }
            validateEmail(userExist, user);
        }
        log.info("Пользователь {} изменён", userId);
        return userStorage.update(user, userId);
    }

    public User update(User user, Integer userId) {
        return userStorage.update(user, user.getId());
    }

    public void delete(Integer id) {
        userStorage.delete(id);
    }

    private void validateEmail(User userExist, User newUser) {
        if (userExist.getEmail().equals(newUser.getEmail())) {
            throw new ConflictException("Такой email " + userExist.getEmail() + " уже используется");
        }
    }


}
