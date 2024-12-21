package ru.practicum.server.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class ItemWithComments {
    Item item;
    List<String> comments;

}
