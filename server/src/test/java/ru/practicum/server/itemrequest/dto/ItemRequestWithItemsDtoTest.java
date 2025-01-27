package ru.practicum.server.itemrequest.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.api.dto.ItemDto;
import ru.practicum.api.dto.ItemRequestWithItemsDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestWithItemsDtoTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialization() throws IOException {
        ItemRequestWithItemsDto itemRequestWithItemsDto = new ItemRequestWithItemsDto();
        itemRequestWithItemsDto.setId(1L);
        itemRequestWithItemsDto.setDescription("Request description");
        itemRequestWithItemsDto.setCreated(LocalDateTime.of(2024, 12, 15, 14, 30, 0));  // Задаем дату
        itemRequestWithItemsDto.setItems(Arrays.asList(new ItemDto(), new ItemDto()));

        String json = objectMapper.writeValueAsString(itemRequestWithItemsDto);

        assertThat(json).contains("\"created\":\"2024-12-15T14:30:00\"");
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"description\":\"Request description\"");

        assertThat(json).contains("\"items\":[{\"id\":null,\"name\":null,\"description\":null,\"available\":null,\"requestId\":null},{\"id\":null,\"name\":null,\"description\":null,\"available\":null,\"requestId\":null}]");
    }

    @Test
    void testDeserialization() throws IOException {
        String json = "{\"id\":1,\"description\":\"Request description\",\"created\":\"2024-12-15T14:30:00\",\"items\":[{},{}]}";

        ItemRequestWithItemsDto itemRequestWithItemsDto = objectMapper.readValue(json, ItemRequestWithItemsDto.class);

        assertThat(itemRequestWithItemsDto.getId()).isEqualTo(1L);
        assertThat(itemRequestWithItemsDto.getDescription()).isEqualTo("Request description");
        assertThat(itemRequestWithItemsDto.getCreated()).isEqualTo(LocalDateTime.of(2024, 12, 15, 14, 30, 0));

        assertThat(itemRequestWithItemsDto.getItems()).hasSize(2);
    }
}
