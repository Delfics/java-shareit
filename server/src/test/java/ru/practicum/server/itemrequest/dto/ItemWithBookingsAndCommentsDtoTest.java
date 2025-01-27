package ru.practicum.server.itemrequest.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.api.dto.ItemWithBookingsAndCommentsDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemWithBookingsAndCommentsDtoTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialization() throws IOException {
        ItemWithBookingsAndCommentsDto itemDto = new ItemWithBookingsAndCommentsDto();
        itemDto.setId(1L);
        itemDto.setName("Item name");
        itemDto.setDescription("Item description");
        itemDto.setAvailable(true);
        itemDto.setNextBooking(LocalDateTime.of(2024, 12, 15, 14, 30, 0));
        itemDto.setLastBooking(LocalDateTime.of(2024, 12, 14, 12, 15, 0));
        itemDto.setComments(Arrays.asList("Comment 1", "Comment 2"));

        String json = objectMapper.writeValueAsString(itemDto);

        assertThat(json).contains("\"nextBooking\":\"2024-12-15T14:30:00\"");
        assertThat(json).contains("\"lastBooking\":\"2024-12-14T12:15:00\"");

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Item name\"");
        assertThat(json).contains("\"description\":\"Item description\"");
        assertThat(json).contains("\"available\":true");

        assertThat(json).contains("\"comments\":[\"Comment 1\",\"Comment 2\"]");
    }

    @Test
    void testDeserialization() throws IOException {
        String json = "{\"id\":1,\"name\":\"Item name\",\"description\":\"Item description\",\"available\":true," +
                "\"nextBooking\":\"2024-12-15T14:30:00\",\"lastBooking\":\"2024-12-14T12:15:00\",\"comments\":[\"Comment 1\",\"Comment 2\"]}";

        ItemWithBookingsAndCommentsDto itemDto = objectMapper.readValue(json, ItemWithBookingsAndCommentsDto.class);

        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("Item name");
        assertThat(itemDto.getDescription()).isEqualTo("Item description");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getNextBooking()).isEqualTo(LocalDateTime.of(2024, 12, 15, 14, 30, 0));
        assertThat(itemDto.getLastBooking()).isEqualTo(LocalDateTime.of(2024, 12, 14, 12, 15, 0));

        assertThat(itemDto.getComments()).containsExactly("Comment 1", "Comment 2");
    }
}
