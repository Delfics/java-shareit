package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.Instant;

@Data
public class ItemRequest {
    Long id;
    String description;
    User requestor;
    Instant created;
}
