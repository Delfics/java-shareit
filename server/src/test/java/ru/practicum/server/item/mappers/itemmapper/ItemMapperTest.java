package ru.practicum.server.item.mappers.itemmapper;

import org.junit.jupiter.api.Test;
import ru.practicum.api.dto.ItemDto;
import ru.practicum.api.dto.ItemWithBookingsAndCommentsDto;
import ru.practicum.server.item.mappers.ItemMapper;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.model.ItemWithBookingsAndComments;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemMapperTest {

    @Test
    void testToItem() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Item Name");
        itemDto.setDescription("Item Description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(2L);

        Item item = ItemMapper.toItem(itemDto);

        assertThat(item).isNotNull();
        assertThat(item.getId()).isEqualTo(itemDto.getId());
        assertThat(item.getName()).isEqualTo(itemDto.getName());
        assertThat(item.getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(item.getAvailable()).isEqualTo(itemDto.getAvailable());
        assertThat(item.getRequestId()).isEqualTo(itemDto.getRequestId());
    }

    @Test
    void testToItemDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item Name");
        item.setDescription("Item Description");
        item.setAvailable(true);
        item.setRequestId(2L);

        ItemDto itemDto = ItemMapper.toItemDto(item);

        assertThat(itemDto).isNotNull();
        assertThat(itemDto.getId()).isEqualTo(item.getId());
        assertThat(itemDto.getName()).isEqualTo(item.getName());
        assertThat(itemDto.getDescription()).isEqualTo(item.getDescription());
        assertThat(itemDto.getAvailable()).isEqualTo(item.getAvailable());
        assertThat(itemDto.getRequestId()).isEqualTo(item.getRequestId());
    }

    @Test
    void testToItemWithBookingsAndCommentsDto() {
        ItemWithBookingsAndComments itemWithBookingsAndComments = new ItemWithBookingsAndComments();
        Item item = new Item();
        item.setId(1L);
        item.setName("Item Name");
        item.setDescription("Item Description");
        item.setAvailable(true);

        itemWithBookingsAndComments.setItem(item);
        itemWithBookingsAndComments.setLastBookingTime(LocalDateTime.of(2024, 12, 15, 14, 30, 0));
        itemWithBookingsAndComments.setNextBookingTime(LocalDateTime.of(2024, 12, 16, 14, 30, 0));
        itemWithBookingsAndComments.setComments(Collections.singletonList("Great item"));

        ItemWithBookingsAndCommentsDto dto = ItemMapper.toItemWithBookingsAndCommentsDto(itemWithBookingsAndComments);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(item.getId());
        assertThat(dto.getName()).isEqualTo(item.getName());
        assertThat(dto.getDescription()).isEqualTo(item.getDescription());
        assertThat(dto.getAvailable()).isEqualTo(item.getAvailable());
        assertThat(dto.getLastBooking()).isEqualTo(itemWithBookingsAndComments.getLastBookingTime());
        assertThat(dto.getNextBooking()).isEqualTo(itemWithBookingsAndComments.getNextBookingTime());
        assertThat(dto.getComments()).containsExactly("Great item");
    }
}
