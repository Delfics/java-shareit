package ru.practicum.shareit.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserDto {
    Long id;
    @NotNull
    @Pattern(regexp = "^(?!,*$).+", message = "Имя не может быть пустым или содержать один символ")
    String name;
    @NotNull
    @Pattern(regexp = "^(?!,*$).+", message = "Почта не может быть пустым или содержать один символ")
    String email;
}
