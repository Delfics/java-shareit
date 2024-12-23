package ru.practicum.server.itemrequest.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.api.dto.ItemRequestDto;
import ru.practicum.api.dto.ItemRequestWithItemsDto;
import ru.practicum.api.dto.UserDto;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.itemrequest.mappers.ItemRequestMapper;
import ru.practicum.server.itemrequest.model.ItemRequest;
import ru.practicum.server.itemrequest.model.ItemRequestWithItems;
import ru.practicum.server.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemRequestMapperTest {

    @Test
    void testToItemRequestDto() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Test description");
        itemRequest.setCreated(LocalDateTime.of(2024, 12, 15, 14, 30, 0));

        User user = new User();
        user.setId(2L);
        user.setName("John Doe");
        itemRequest.setRequestor(user);

        ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(itemRequest);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(itemRequest.getId());
        assertThat(dto.getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(dto.getCreated()).isEqualTo(itemRequest.getCreated());
        assertThat(dto.getRequestor().getId()).isEqualTo(user.getId());
        assertThat(dto.getRequestor().getName()).isEqualTo(user.getName());
    }

    @Test
    void testToItemRequest() {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("Test description");
        dto.setCreated(LocalDateTime.of(2024, 12, 15, 14, 30, 0));

        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setName("John Doe");
        dto.setRequestor(userDto);

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(dto);

        assertThat(itemRequest).isNotNull();
        assertThat(itemRequest.getId()).isEqualTo(dto.getId());
        assertThat(itemRequest.getDescription()).isEqualTo(dto.getDescription());
        assertThat(itemRequest.getCreated()).isEqualTo(dto.getCreated());
        assertThat(itemRequest.getRequestor().getId()).isEqualTo(userDto.getId());
        assertThat(itemRequest.getRequestor().getName()).isEqualTo(userDto.getName());
    }

    @Test
    void testToItemRequestWithItemsDto() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Test description");
        itemRequest.setCreated(LocalDateTime.of(2024, 12, 15, 14, 30, 0));

        Item item = new Item();
        item.setId(1L);
        item.setName("Item name");
        item.setDescription("Item description");
        item.setAvailable(true);

        List<Item> items = new ArrayList<>();
        items.add(item);

        ItemRequestWithItems itemRequestWithItems = new ItemRequestWithItems(itemRequest, items);

        ItemRequestWithItemsDto dto = ItemRequestMapper.toItemRequestWithItemsDto(itemRequestWithItems);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(itemRequest.getId());
        assertThat(dto.getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(dto.getCreated()).isEqualTo(itemRequest.getCreated());
        assertThat(dto.getItems()).isNotEmpty();
        assertThat(dto.getItems().get(0).getId()).isEqualTo(item.getId());
        assertThat(dto.getItems().get(0).getName()).isEqualTo(item.getName());
    }
}
