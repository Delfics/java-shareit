package ru.practicum.server.item.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.api.dto.CommentDtoRequired;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoRequiredTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialization() throws IOException {
        CommentDtoRequired commentDto = new CommentDtoRequired();
        commentDto.setId(1L);
        commentDto.setText("This is a comment.");
        commentDto.setAuthorName("John Doe");
        commentDto.setCreated(LocalDateTime.of(2024, 12, 15, 14, 30, 0));

        String json = objectMapper.writeValueAsString(commentDto);

        assertThat(json).contains("\"created\":\"2024-12-15T14:30:00\"");
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"text\":\"This is a comment.\"");
        assertThat(json).contains("\"authorName\":\"John Doe\"");
    }

    @Test
    void testDeserialization() throws IOException {
        String json = "{\"id\":1,\"text\":\"This is a comment.\",\"authorName\":\"John Doe\",\"created\":\"2024-12-15T14:30:00\"}";

        CommentDtoRequired commentDto = objectMapper.readValue(json, CommentDtoRequired.class);

        assertThat(commentDto.getId()).isEqualTo(1L);
        assertThat(commentDto.getText()).isEqualTo("This is a comment.");
        assertThat(commentDto.getAuthorName()).isEqualTo("John Doe");
        assertThat(commentDto.getCreated()).isEqualTo(LocalDateTime.of(2024, 12, 15, 14, 30, 0));
    }
}
