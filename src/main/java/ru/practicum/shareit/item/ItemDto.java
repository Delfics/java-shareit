package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ItemDto {
    Long id;
    @NotNull
    @Pattern(regexp = "^(?!,*$).+", message = "Имя не может быть пустым или содержать один символ")
    String name;
    @NotNull
    @Pattern(regexp = "^(?!,*$).+", message = "Описание не может быть пустым или содержать один символ")
    String description;
    @NotNull
    Boolean available;
}
