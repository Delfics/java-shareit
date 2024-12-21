package ru.practicum.server.item.validator;

import lombok.experimental.UtilityClass;
import ru.practicum.server.exception.ValidationException;
import ru.practicum.server.item.model.Item;


@UtilityClass
public class Valid {

    public void throwExIfUserIdNull(Long userId, Item item) {
        if (userId == null) {
            throw new ValidationException("Необходимо указать id пользователя для " + item.getName());
        }
    }

    public void throwExIfNameOrDescriptionAreEmptyAndAvailableIsNull(Item item) {
        if (item.getName().isEmpty() || item.getDescription().isEmpty() || item.getAvailable() == null) {
            throw new ValidationException("Необходимо указать Name или Description или Available для item " + item.getId());
        }
    }
}
