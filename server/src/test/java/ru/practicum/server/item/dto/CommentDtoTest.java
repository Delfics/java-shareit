package ru.practicum.server.item.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.api.dto.CommentDto;
import ru.practicum.api.dto.ItemDto;
import ru.practicum.api.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialization() throws IOException {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("This is a comment.");
        commentDto.setCreated(LocalDateTime.of(2024, 12, 15, 14, 30, 0));  // Задаем дату
        commentDto.setItem(new ItemDto());
        commentDto.setAuthor(new UserDto());


        String json = objectMapper.writeValueAsString(commentDto);

        assertThat(json).contains("\"created\":\"2024-12-15T14:30:00\"");
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"text\":\"This is a comment.\"");
    }

    @Test
    void testDeserialization() throws IOException {
        String json = "{\"id\":1,\"text\":\"This is a comment.\",\"created\":\"2024-12-15T14:30:00\",\"item\":{},\"author\":{}}";

        CommentDto commentDto = objectMapper.readValue(json, CommentDto.class);

        assertThat(commentDto.getCreated()).isEqualTo(LocalDateTime.of(2024, 12, 15, 14, 30, 0));
        assertThat(commentDto.getId()).isEqualTo(1L);
        assertThat(commentDto.getText()).isEqualTo("This is a comment.");
    }
}
