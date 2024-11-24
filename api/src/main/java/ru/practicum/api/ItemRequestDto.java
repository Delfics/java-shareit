package ru.practicum.api;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemRequestDto {
    Long id;
    String description;
    UserDto requestor;
    LocalDateTime created;
}