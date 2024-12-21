package ru.practicum.gateway.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.api.dto.CommentDto;
import ru.practicum.api.dto.ItemDto;
import ru.practicum.gateway.utils.HttpProperties;

@RestController
@Slf4j
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(HttpProperties.xSharerUserId) Long userId, @RequestBody @Valid ItemDto itemDto) {
        log.info("Creating item={}, userId={}", itemDto, userId);
        return itemClient.createItem(userId, itemDto);
    }

    @GetMapping()
    public ResponseEntity<Object> getItems(@RequestHeader(HttpProperties.xSharerUserId) Long userId) {
        log.info("Get users userId={}", userId);
        return itemClient.getAll(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getByIdItemWithComments(@PathVariable Long itemId, @RequestHeader(HttpProperties.xSharerUserId) Long userId) {
        log.info("Get - getById. Входные параметры itemId={}, userId={}", itemId, userId);
        return itemClient.getByIdItemWithComments(itemId, userId);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createComment(@RequestBody CommentDto commentDto, @PathVariable("itemId") Long itemId,
                                                @RequestHeader(HttpProperties.xSharerUserId) Long userId) {
        log.info("Post - createComment. Входные параметры commentDto={}, itemId={}, userId={} ", commentDto, itemId, userId);
        return itemClient.createComment(commentDto, itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItem(@PathVariable Long itemId, @RequestBody @Valid ItemDto itemDto,
                                            @RequestHeader(HttpProperties.xSharerUserId) Long userId) {
        log.info("Patch - patch. Входные параметры itemId {},  itemDto - {} , userId {} ", itemId, itemDto.toString(), userId);
        return itemClient.patchItem(itemId, itemDto, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        log.info("Get - searchItems. Входные параметры text={}", text);
        return itemClient.searchItems(text);
    }
}
