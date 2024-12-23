package ru.practicum.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    Long id;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime start;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime end;
    Long itemId;
    ItemDto item;
    UserDto booker;
    Status status;
}