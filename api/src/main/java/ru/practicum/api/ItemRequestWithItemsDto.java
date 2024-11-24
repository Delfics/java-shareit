package ru.practicum.api;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestWithItemsDto {
    Long id;
    String description;
    LocalDateTime created;
    List<ItemDto> items;
}