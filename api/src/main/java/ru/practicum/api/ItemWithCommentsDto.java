package ru.practicum.api;

import lombok.Data;

import java.util.List;

@Data
public class ItemWithCommentsDto {
    Long id;
    String name;
    String description;
    Boolean available;
    List<String> comments;
}