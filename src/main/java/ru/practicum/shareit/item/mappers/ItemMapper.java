package ru.practicum.shareit.item.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.*;

@UtilityClass
public class ItemMapper {
    public Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

    public ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        return itemDto;
    }

    public ItemWithBookingsAndCommentsDto toItemWithBookingsAndCommentsDto(ItemWithBookingsAndComments item) {
        ItemWithBookingsAndCommentsDto itemWithBookingsAndCommentsDto = new ItemWithBookingsAndCommentsDto();
        itemWithBookingsAndCommentsDto.setId(item.getItem().getId());
        itemWithBookingsAndCommentsDto.setName(item.getItem().getName());
        itemWithBookingsAndCommentsDto.setDescription(item.getItem().getDescription());
        itemWithBookingsAndCommentsDto.setAvailable(item.getItem().getAvailable());
        itemWithBookingsAndCommentsDto.setLastBooking(item.getLastBookingTime());
        itemWithBookingsAndCommentsDto.setNextBooking(item.getNextBookingTime());
        itemWithBookingsAndCommentsDto.setComments(item.getComments());
        return itemWithBookingsAndCommentsDto;
    }
}
