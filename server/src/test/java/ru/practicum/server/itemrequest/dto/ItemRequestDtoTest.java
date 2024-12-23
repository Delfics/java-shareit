package ru.practicum.server.itemrequest.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.api.dto.ItemRequestDto;
import ru.practicum.api.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialization() throws IOException {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Item description");
        itemRequestDto.setRequestor(new UserDto());
        itemRequestDto.setCreated(LocalDateTime.of(2024, 12, 15, 14, 30, 0));

        String json = objectMapper.writeValueAsString(itemRequestDto);

        assertThat(json).contains("\"created\":\"2024-12-15T14:30:00\"");
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"description\":\"Item description\"");
    }

    @Test
    void testDeserialization() throws IOException {
        String json = "{\"id\":1,\"description\":\"Item description\",\"requestor\":{},\"created\":\"2024-12-15T14:30:00\"}";

        ItemRequestDto itemRequestDto = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(itemRequestDto.getId()).isEqualTo(1L);
        assertThat(itemRequestDto.getDescription()).isEqualTo("Item description");
        assertThat(itemRequestDto.getCreated()).isEqualTo(LocalDateTime.of(2024, 12, 15, 14, 30, 0));

        assertThat(itemRequestDto.getRequestor()).isNotNull();
    }
}
