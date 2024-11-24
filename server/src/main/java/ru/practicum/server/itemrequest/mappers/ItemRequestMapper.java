package ru.practicum.server.itemrequest.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.api.ItemDto;
import ru.practicum.api.ItemRequestDto;
import ru.practicum.api.ItemRequestWithItemsDto;
import ru.practicum.server.item.mappers.ItemMapper;
import ru.practicum.server.itemrequest.model.ItemRequest;
import ru.practicum.server.itemrequest.model.ItemRequestWithItems;
import ru.practicum.server.user.mappers.UserMapper;


import java.util.List;

@UtilityClass
public class ItemRequestMapper {
    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setRequestor(UserMapper.toUserDto(itemRequest.getRequestor()));
        itemRequestDto.setCreated(itemRequest.getCreated());
        return itemRequestDto;
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        if (itemRequestDto.getId() != null) {
            itemRequest.setId(itemRequestDto.getId());
        }
        itemRequest.setDescription(itemRequestDto.getDescription());
        if (itemRequestDto.getRequestor() != null) {
            itemRequest.setRequestor(UserMapper.toUser(itemRequestDto.getRequestor()));
        }
        if (itemRequestDto.getCreated() != null) {
            itemRequest.setCreated(itemRequestDto.getCreated());
        }
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
