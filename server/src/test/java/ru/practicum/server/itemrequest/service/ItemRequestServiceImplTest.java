package ru.practicum.server.itemrequest.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.service.ItemServiceImplJpa;
import ru.practicum.server.itemrequest.model.ItemRequest;
import ru.practicum.server.itemrequest.model.ItemRequestWithItems;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.service.UserServiceImplJpa;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
public class ItemRequestServiceImplTest {
    @Autowired
    ItemRequestServiceImpl itemRequestService;

    @Autowired
    ItemServiceImplJpa itemServiceImplJpa;

    @Autowired
    UserServiceImplJpa userServiceImplJpa;

    User requestor;
    User owner;
    Item item;
    ItemRequest itemRequest;
    @Autowired
    private ItemRequestServiceImpl itemRequestServiceImpl;

    @BeforeEach
    void setUp() {
        requestor = new User();
        requestor.setName("Test name Requestor");
        requestor.setEmail("Test email Requestor@gmail.com");
        userServiceImplJpa.create(requestor);

        owner = new User();
        owner.setName("Test owner Owner");
        owner.setEmail("Test email Owner@gmail.com");
        userServiceImplJpa.create(owner);

        item = new Item();
        item.setName("Test item Item");
        item.setDescription("Test item Description");
        item.setAvailable(true);
        item.setOwner(owner);

        itemRequest = new ItemRequest();
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("Test item Description");
        itemRequest.setRequestor(requestor);
    }

    @Test
    public void testCreateItemRequest() {
        ItemRequest itemRequest1 = itemRequestServiceImpl.createItemRequest(itemRequest, requestor.getId());

        assertNotNull(itemRequest1, "ItemRequest создан");
        assertEquals(itemRequest1.getCreated(), itemRequest.getCreated(), "Время создания совпадает");
        assertEquals(itemRequest1.getDescription(), itemRequest.getDescription(), "Описание itemRequest совпадает");
        assertEquals(itemRequest1.getRequestor().getName(), requestor.getName(), "Имя requestor совпадает");
        assertEquals(itemRequest1.getRequestor().getEmail(), requestor.getEmail(), "Email requestor совпадает");
    }

    @Test
    public void testFindAllOtherItemRequests() {
        int size = 2;
        itemRequestServiceImpl.createItemRequest(itemRequest, requestor.getId());

        User anotherRequestor1 = new User();
        anotherRequestor1.setName("Test another requestor");
        anotherRequestor1.setEmail("Test another requestor@gmail.com");
        userServiceImplJpa.create(anotherRequestor1);

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setCreated(LocalDateTime.now());
        itemRequest1.setDescription("Test item Description");
        itemRequest1.setRequestor(anotherRequestor1);
        itemRequestServiceImpl.createItemRequest(itemRequest1, anotherRequestor1.getId());

        User anotherRequestor2 = new User();
        anotherRequestor2.setName("Test another requestor2");
        anotherRequestor2.setEmail("Test another requestor2@gmail.com");
        userServiceImplJpa.create(anotherRequestor2);

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setCreated(LocalDateTime.now());
        itemRequest2.setDescription("Test item Description");
        itemRequest2.setRequestor(anotherRequestor1);
        itemRequestServiceImpl.createItemRequest(itemRequest2, anotherRequestor2.getId());

        List<ItemRequest> allOtherItemRequests = itemRequestServiceImpl.findAllOtherItemRequests(requestor.getId());

        assertEquals(allOtherItemRequests.size(), size, "Найдены все itemrequests кроме первого itemrequest");
        assertEquals(allOtherItemRequests.get(1).getRequestor().getName(), anotherRequestor1.getName(),
                "Name соответсвует anotherRequestor1");
        assertEquals(allOtherItemRequests.get(0).getRequestor().getName(), anotherRequestor2.getName(),
                "Name соответсвует anotherRequestor2");
    }

    @Test
    public void testfindAllItemRequestsWithAllItemsForUser() {
        int size = 2;
        ItemRequest itemRequest1 = itemRequestServiceImpl.createItemRequest(itemRequest, requestor.getId());

        item.setRequestId(itemRequest1.getId());
        itemServiceImplJpa.createItem(item, owner.getId());

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setCreated(LocalDateTime.now());
        itemRequest2.setDescription("Test item Description2");
        itemRequest2.setRequestor(requestor);
        itemRequestServiceImpl.createItemRequest(itemRequest2, requestor.getId());

        Item item2 = new Item();
        item2.setName("Test item Item2");
        item2.setDescription("Test item Description2");
        item2.setAvailable(true);
        item2.setOwner(owner);
        item2.setRequestId(itemRequest2.getId());
        itemServiceImplJpa.createItem(item2, owner.getId());

        List<ItemRequestWithItems> result = itemRequestServiceImpl.findAllItemRequestsWithAllItemsForUser(requestor.getId());

        Item itemGot2 = result.get(0).getItems().get(0);

        Item itemGot = result.get(1).getItems().get(0);

        assertEquals(result.size(), size, "Количество ItemRequest (2) данного requestor соответсвует");

        assertEquals(itemGot2.getRequestId(), itemRequest2.getId(), "Id полученного Item2 соответсвует созданному ItemRequest");
        assertEquals(itemGot2.getName(), item2.getName(), "Имена полученного из метода и созданного Item2 соответсвуют");

        assertEquals(itemGot.getRequestId(), itemRequest1.getId(), "Id полученного item соответсвует созданному ItemRequest");
        assertEquals(itemGot.getName(), item.getName(), "Имена полученного из метода и созданного Item соответсвуют");
    }

    @Test
    public void testFindItemRequestByIdWithAllItems() {
        int size = 2;
        itemRequestServiceImpl.createItemRequest(itemRequest, requestor.getId());
        item.setRequestId(itemRequest.getId());
        itemServiceImplJpa.createItem(item, owner.getId());

        Item item2 = new Item();
        item2.setName("Test item Item2");
        item2.setDescription("Test item Description");
        item2.setAvailable(true);
        item2.setOwner(owner);
        item2.setRequestId(itemRequest.getId());
        itemServiceImplJpa.createItem(item2, owner.getId());

        ItemRequestWithItems itemRequestByIdWithAllItems = itemRequestServiceImpl.findItemRequestByIdWithAllItems(itemRequest.getId());
        List<Item> items = itemRequestByIdWithAllItems.getItems();

        assertEquals(itemRequestByIdWithAllItems.getItemRequest().getDescription(), itemRequest.getDescription(),
                "Описание соответсвует созданному ItemRequest и полуенному из itemRequestByIdWithAllItems");
        assertEquals(itemRequestByIdWithAllItems.getItemRequest().getCreated(), itemRequest.getCreated(),
                "Время совпадает ItemRequest");
        assertEquals(items.size(), size, "Количество найденных items(2) соответсвует");
    }
}
