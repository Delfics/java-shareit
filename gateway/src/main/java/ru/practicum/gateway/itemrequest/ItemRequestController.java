package ru.practicum.gateway.itemrequest;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.api.dto.ItemRequestDto;
import ru.practicum.api.utils.HttpProperties;


@Slf4j
@RequestMapping("/requests")
@RestController
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @Autowired
    public ItemRequestController(final ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }

    @GetMapping()
    public ResponseEntity<Object> findAllItemRequestsWithItemsForEach(@RequestHeader(HttpProperties.xSharerUserId) Long userId) {
        log.info("Get - findAllItemRequestsWithItemsForEach. Входные параметры userId={}", userId);
        return itemRequestClient.findAllItemRequestsWithItemsForEach(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findItemRequestByIdWithItemsForEach(@RequestHeader(HttpProperties.xSharerUserId) Long userId,
                                                                      @PathVariable("requestId") Long requestId) {
        log.info("Get - findItemRequestByIdWithItemsForEach. Входные параметры requestId={}", requestId);
        return itemRequestClient.findItemRequestByIdWithItemsForEach(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllOtherItemRequests(@RequestHeader(HttpProperties.xSharerUserId) Long userId) {
        log.info("Get - getAllOtherItemRequests. Входные параметры userId={}", userId);
        return itemRequestClient.getAllOtherItemRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItemRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                                    @RequestHeader(HttpProperties.xSharerUserId) Long userId) {
        log.info("Post - createItemRequest. Входные параметры itemRequestDto={} , userId={} ", itemRequestDto, userId);
        return itemRequestClient.createItemRequest(itemRequestDto, userId);
    }
}
