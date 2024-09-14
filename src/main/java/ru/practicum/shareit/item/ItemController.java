package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.utils.HttpProperties;

import java.util.Collection;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemServiceImpl itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@Valid @RequestBody ItemDto itemDto, @RequestHeader(HttpProperties.HEADER) Long userId) {
        log.info("ItemController Запрос Post - create. Входные параметры itemDto - {} , userId {} ", itemDto.toString(), userId);
        return ItemMapper.toItemDto(itemService.create(ItemMapper.toItem(itemDto), userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto patch(@PathVariable Long itemId, @RequestBody ItemDto itemDto,
                             @RequestHeader(HttpProperties.HEADER) Long userId) {
        log.info("ItemController Запрос Patch - patch. Входные параметры itemId {},  itemDto - {} , userId {} ", itemId, itemDto.toString(), userId);
        return ItemMapper.toItemDto(itemService.patch(itemId, ItemMapper.toItem(itemDto), userId));
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable Long itemId) {
        log.info("ItemController Запрос Get - getById. Входные параметры itemId {}", itemId);
        return ItemMapper.toItemDto(itemService.getById(itemId));
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader(HttpProperties.HEADER) Long userId) {
        log.info("ItemController Запрос Get - getItems. Входные параметры userId {}", userId);
        Collection<Item> itemsOwner = itemService.getItemsOwner(userId);
        return itemsOwner.stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("ItemController Запрос Get - searchItems. Входные параметры text {}", text);
        Collection<Item> items = itemService.searchItems(text);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }
}
