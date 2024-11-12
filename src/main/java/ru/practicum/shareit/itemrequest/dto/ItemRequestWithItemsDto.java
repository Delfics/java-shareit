package ru.practicum.shareit.itemrequest.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestWithItemsDto {
    Long id;
    String description;
    LocalDateTime created;
    List<ItemDto> items;
}
