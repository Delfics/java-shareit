package ru.practicum.shareit.item.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.dto.ItemDto;
import ru.practicum.shareit.dto.ItemWithBookingsAndCommentsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemWithBookingsAndComments;

@UtilityClass
public class ItemMapper {
    public Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        if (itemDto.getRequestId() != null) {
            /*ItemRequest itemRequest = new ItemRequest();
            itemRequest.setId(itemDto.getRequestId());*/
            /*item.setItemRequest(itemRequest);*/
            item.setRequestId(itemDto.getRequestId());
        }
        return item;
    }

    public ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        /*if (item.getItemRequest() != null && item.getItemRequest().getId() != null ) {
            itemDto.setRequestId(item.getItemRequest().getId());
        }*/
        if (item.getRequestId() != null) {
            itemDto.setRequestId(item.getRequestId());
        }
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
