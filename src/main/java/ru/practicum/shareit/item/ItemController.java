package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.jpa.ItemServiceImplJpa;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentDtoRequired;
import ru.practicum.shareit.item.comment.mappers.CommentMapper;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.utils.HttpProperties;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemServiceImplJpa itemService;

    @Autowired
    public ItemController(ItemServiceImplJpa itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(HttpProperties.xSharerUserId) Long userId) {
        log.info("ItemController Запрос Post - createItem. Входные параметры itemDto - {} , userId {} ", itemDto.toString(), userId);
        return ItemMapper.toItemDto(itemService.createItem(ItemMapper.toItem(itemDto), userId));
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDtoRequired createComment(@RequestBody CommentDto commentDto, @PathVariable("itemId") Long itemId,
                                            @RequestHeader(HttpProperties.xSharerUserId) Long userId) {
        log.info("ItemController Запрос Post - createComment. Входные параметры itemDto - {} , userId {} ", commentDto.toString(), userId);
        CommentDto commentDto1 = itemService.addAuthorToCommentDto(commentDto, userId);
        commentDto1 = itemService.addItemToCommentDto(commentDto1, itemId);
        return CommentMapper.toDto(itemService.createComment(CommentMapper.toComment(commentDto1), userId, itemId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@PathVariable Long itemId, @RequestBody ItemDto itemDto,
                             @RequestHeader(HttpProperties.xSharerUserId) Long userId) {
        log.info("ItemController Запрос Patch - patch. Входные параметры itemId {},  itemDto - {} , userId {} ", itemId, itemDto.toString(), userId);
        return ItemMapper.toItemDto(itemService.patch(itemId, ItemMapper.toItem(itemDto), userId));
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingsAndCommentsDto getByIdItemWithComments(@PathVariable Long itemId, @RequestHeader(HttpProperties.xSharerUserId) Long userId) {
        log.info("ItemController Запрос Get - getById. Входные параметры itemId {}", itemId);
        return ItemMapper.toItemWithBookingsAndCommentsDto(itemService.findItemWithComments(itemId));
    }

    @GetMapping
    public List<ItemWithBookingsAndCommentsDto> getItems(@RequestHeader(HttpProperties.xSharerUserId) Long userId) {
        log.info("ItemController Запрос Get - getItems. Входные параметры userId {}", userId);
        List<ItemWithBookingsAndComments> itemsWithBookingsAndComments = itemService.findItemsWithCommentsBookingByUserId(userId);
        return itemsWithBookingsAndComments.stream()
                .map(ItemMapper::toItemWithBookingsAndCommentsDto)
                .toList();
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("ItemController Запрос Get - searchItems. Входные параметры text {}", text);
        Collection<Item> items = itemService.findItemsByText(text);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

}
