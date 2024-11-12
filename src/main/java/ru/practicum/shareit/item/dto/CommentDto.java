package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    Long id;
    String text;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime created;
    ItemDto item;
    UserDto author;
}