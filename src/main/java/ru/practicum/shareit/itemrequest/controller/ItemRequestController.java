package ru.practicum.shareit.itemrequest.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.itemrequest.dto.ItemRequestDto;
import ru.practicum.shareit.itemrequest.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.itemrequest.mappers.ItemRequestMapper;
import ru.practicum.shareit.itemrequest.model.ItemRequest;
import ru.practicum.shareit.itemrequest.model.ItemRequestWithItems;
import ru.practicum.shareit.itemrequest.service.ItemRequestServiceImpl;
import ru.practicum.shareit.utils.HttpProperties;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    ItemRequestServiceImpl itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestServiceImpl itemRequestService) {
        this.itemRequestService = itemRequestService;
    }


    @GetMapping()
    public List<ItemRequestWithItemsDto> findAllItemRequestsWithItemsForEach(@RequestHeader(HttpProperties.xSharerUserId) Long userId) {
        log.info("Запрос Get - findAllItemRequestsWithItemsForEach. Входные параметры userId {}", userId);
        List<ItemRequestWithItems> allItemRequestsWithItemsForEach = itemRequestService.findAllItemRequestsWithItemsForEach(userId);
        List<ItemRequestWithItemsDto> result = new ArrayList<>();
        for (ItemRequestWithItems itemRequestWithItems : allItemRequestsWithItemsForEach) {
            ItemRequestWithItemsDto itemRequestWithItemsDto = ItemRequestMapper.toItemRequestWithItemsDto(itemRequestWithItems);
            result.add(itemRequestWithItemsDto);
        }
        return result;
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemsDto findItemRequestByIdWithItemsForEach(@RequestHeader(HttpProperties.xSharerUserId) Long userId,
                                                           @PathVariable Long requestId) {
        log.info("Запрос Get - findItemRequestByIdWithItemsForEach. Входные параметры requestId {}", requestId);
        ItemRequestWithItems itemRequestByIdWithItemsForEach = itemRequestService.findItemRequestByIdWithItemsForEach(requestId);
        return ItemRequestMapper.toItemRequestWithItemsDto(itemRequestByIdWithItemsForEach);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllOtherItemRequests(@RequestHeader(HttpProperties.xSharerUserId) Long userId) {
        log.info("Запрос Get - getAllOtherItemRequests. Входные параметры {}", userId);
        List<ItemRequest> allOtherItemRequests = itemRequestService.findAllOtherItemRequests(userId);
        List<ItemRequestDto> result = new ArrayList<>();
        for (ItemRequest itemRequest : allOtherItemRequests) {
            ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
            result.add(itemRequestDto);
        }
        return result;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createItemRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                            @RequestHeader(HttpProperties.xSharerUserId) Long userId) {
        log.info("Запрос Post - createItemRequest. Входные параметры itemRequestDto - {} , userId {} ", itemRequestDto.toString(), userId);
        return ItemRequestMapper.toItemRequestDto(itemRequestService.createItemRequest(ItemRequestMapper.toItemRequest(itemRequestDto), userId));
    }
}
