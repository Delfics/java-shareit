package ru.practicum.shareit.itemrequest.dto;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class ItemRequestDto {
    Long id;
    String description;
    User requestor;
    LocalDateTime created;
}
