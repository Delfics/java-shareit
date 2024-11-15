package ru.practicum.shareit.itemrequest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImplJpa;
import ru.practicum.shareit.itemrequest.model.ItemRequest;
import ru.practicum.shareit.itemrequest.model.ItemRequestWithItems;
import ru.practicum.shareit.itemrequest.repository.ItemRequestStorageJpa;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ItemRequestServiceImpl {
    private final ItemRequestStorageJpa itemRequestStorage;
    private final UserService userService;
    private final ItemServiceImplJpa itemService;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestStorageJpa itemRequestStorage, UserService userService,
                                  ItemServiceImplJpa itemService ) {
        this.itemRequestStorage = itemRequestStorage;
        this.userService = userService;
        this.itemService = itemService;
    }

    public List<ItemRequest> findAllOtherItemRequests(Long userId) {
        List<ItemRequest> allItemRequests = itemRequestStorage.findAll();

        List<ItemRequest> userItemRequests = itemRequestStorage.findAllItemRequestsByUserId(userId);

        List<ItemRequest> otherItemRequests = allItemRequests.stream()
                .filter(itemRequest -> !userItemRequests.contains(itemRequest)) // Исключаем собственные запросы
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed()) // Сортируем от новых к старым
                .toList();

        return otherItemRequests;
    }


    public List<ItemRequestWithItems> findAllItemRequestsWithItemsForEach(Long userId) {
        List<ItemRequest> allItemRequestsByUserId = itemRequestStorage.findAllItemRequestsByUserId(userId);
        List<Item> itemsByItemRequestRequestorId = itemService.findItemsByItemRequestRequestorId(userId);

        List<ItemRequest> sortedItemRequests = allItemRequestsByUserId.stream()
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .toList();

        List<ItemRequestWithItems> result = new ArrayList<>();

        for (ItemRequest itemRequest : sortedItemRequests) {
            List<Item> associatedItems = itemsByItemRequestRequestorId.stream()
                    .filter(item -> item.getRequestId().equals(itemRequest.getId()))
                    .toList();

            ItemRequestWithItems itemRequestWithItems = new ItemRequestWithItems(itemRequest, associatedItems);
            result.add(itemRequestWithItems);
        }
        return result;
    }




    public ItemRequestWithItems findItemRequestByIdWithItemsForEach(Long requestId) {
        Object[] itemRequestByIdWithItemsForEach = itemRequestStorage.findItemRequestByIdWithItemsForEach(requestId);
        ItemRequest itemRequest = new ItemRequest();
        List<Item> items = new ArrayList<>();
        if (itemRequestByIdWithItemsForEach != null) {
            Object[] newItemRequestByIdWithItemsForEach = (Object[]) itemRequestByIdWithItemsForEach[0];
            for (Object obj : newItemRequestByIdWithItemsForEach) {
                if (obj instanceof ItemRequest) {
                    itemRequest = (ItemRequest) obj;
                } else if (obj instanceof Item) {
                    items.add((Item) obj);
                }
            }
        }
        return new ItemRequestWithItems(itemRequest, items);
    }

    public ItemRequest createItemRequest(ItemRequest itemRequest, Long userId) {
        User requestor = userService.getById(userId);
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequest created = itemRequestStorage.save(itemRequest);
        return created;
    }
}
