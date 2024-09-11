package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.mappers.ItemDtoToItem;
import ru.practicum.shareit.item.mappers.ItemToItemDto;

import java.util.Collection;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") String userId) {
        return ItemToItemDto.toItemDto(itemService.create(ItemDtoToItem.toItem(itemDto), userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@PathVariable Integer itemId, @RequestBody ItemDto itemDto,
                             @RequestHeader("X-Sharer-User-Id") String userId) {
        return ItemToItemDto.toItemDto(itemService.patch(itemId, ItemDtoToItem.toItem(itemDto), userId));
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Integer itemId) {
        return ItemToItemDto.toItemDto(itemService.getById(itemId));
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") String userId) {
        Collection<Item> itemsOwner = itemService.getItemsOwner(userId);
        return itemsOwner.stream()
                .map(ItemToItemDto::toItemDto)
                .toList();
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        Collection<Item> items = itemService.searchItems(text);
        return items.stream()
                .map(ItemToItemDto::toItemDto)
                .toList();
    }
}
