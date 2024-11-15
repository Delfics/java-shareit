package ru.practicum.shareit.itemrequest.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.dto.ItemRequestDto;
import ru.practicum.shareit.itemrequest.model.ItemRequest;


import java.util.List;

@UtilityClass
public class ItemRequestMapper {
    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setRequestor(itemRequest.getRequestor());
        itemRequestDto.setCreated(itemRequest.getCreated());
        return itemRequestDto;
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestDto.getId());
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequestor(itemRequestDto.getRequestor());
        itemRequest.setCreated(itemRequestDto.getCreated());
        return itemRequest;
    }

    public ItemRequestWithItemsDto toItemRequestWithItemsDto(ItemRequestWithItems itemRequestWithItem) {
        ItemRequestWithItemsDto itemRequestWithItemDto = new ItemRequestWithItemsDto();
        ItemRequest itemRequest = itemRequestWithItem.getItemRequest();
        itemRequestWithItemDto.setId(itemRequest.getId());
        itemRequestWithItemDto.setDescription(itemRequest.getDescription());
        itemRequestWithItemDto.setCreated(itemRequest.getCreated());
        List<ItemDto> items = itemRequestWithItem.getItems().stream()
                .map(ItemMapper::toItemDto)
                .toList();
        itemRequestWithItemDto.setItems(items);
        return itemRequestWithItemDto;
    }

    public List<ItemRequestWithItemsDto> toListItemRequestWithItemsDto(List<ItemRequest> itemRequests) {
        return null;
    }

}
