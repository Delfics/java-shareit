package ru.practicum.server.itemrequest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.service.ItemServiceImplJpa;
import ru.practicum.server.itemrequest.model.ItemRequest;
import ru.practicum.server.itemrequest.model.ItemRequestWithItems;
import ru.practicum.server.itemrequest.repository.ItemRequestStorageJpa;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.service.UserService;


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
                                  ItemServiceImplJpa itemService) {
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


    public List<ItemRequestWithItems> findAllItemRequestsWithAllItemsForUser(Long userId) {
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


    public ItemRequestWithItems findItemRequestByIdWithAllItems(Long requestId) {
        Object[] itemRequestByIdWithItemsForEach = itemRequestStorage.findItemRequestByIdWithItemsForEach(requestId);
        ItemRequest itemRequest = new ItemRequest();
        List<Item> items = new ArrayList<>();
        if (itemRequestByIdWithItemsForEach != null) {
            for (Object obj : itemRequestByIdWithItemsForEach) {
                Object[] arrayOfObj = (Object[]) obj;
                if (arrayOfObj[0] instanceof ItemRequest) {
                    itemRequest = (ItemRequest) arrayOfObj[0];
                    if (arrayOfObj[1] instanceof Item item) {
                        items.add(item);
                    }
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
