package ru.practicum.api;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemWithBookingsAndCommentsDto {
    Long id;
    String name;
    String description;
    Boolean available;
    LocalDateTime nextBooking;
    LocalDateTime lastBooking;
    List<String> comments;
}