package ru.practicum.server.itemrequest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.api.dto.ItemRequestDto;
import ru.practicum.api.dto.ItemRequestWithItemsDto;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.itemrequest.mappers.ItemRequestMapper;
import ru.practicum.server.itemrequest.model.ItemRequest;
import ru.practicum.server.itemrequest.model.ItemRequestWithItems;
import ru.practicum.server.itemrequest.service.ItemRequestServiceImpl;
import ru.practicum.server.user.model.User;
import ru.practicum.api.utils.HttpProperties;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {
    @InjectMocks
    private ItemRequestController itemRequestController;
    private final ItemRequestServiceImpl itemRequestService = Mockito.mock(ItemRequestServiceImpl.class);
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private User requestor;
    private final DateTimeFormatter formatJson = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemRequestController).build();

        requestor = new User();
        requestor.setId(1L);
        requestor.setName("Test Requestor name");
        requestor.setEmail("Test requestor228@gmail.com");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setRequestor(requestor);
        itemRequest.setDescription("Test item");
        itemRequest.setCreated(LocalDateTime.now());

        itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Test
    public void testCreateItemRequest() throws Exception {
        LocalDateTime created = itemRequestDto.getCreated();
        String createdString = formatJson.format(created);
        when(itemRequestService.createItemRequest(any(ItemRequest.class), anyLong())).thenReturn(itemRequest);

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpProperties.xSharerUserId, requestor.getId()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(createdString)))
                .andExpect(jsonPath("$.requestor.id", is(itemRequestDto.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$.requestor.name", is(itemRequestDto.getRequestor().getName())))
                .andExpect(jsonPath("$.requestor.email", is(itemRequestDto.getRequestor().getEmail())));

        verify(itemRequestService, times(1)).createItemRequest(any(ItemRequest.class), anyLong());
    }

    @Test
    public void testFindAllItemRequestsWithItemsForEach() throws Exception {
        LocalDateTime created = itemRequestDto.getCreated();
        String createdString = formatJson.format(created);

        User owner = new User();
        owner.setId(1L);
        owner.setName("Test owner name");
        owner.setEmail("Test owner email");

        Item item = new Item();
        item.setId(1L);
        item.setName("Test item name");
        item.setDescription("Test item description");
        item.setOwner(owner);
        item.setRequestId(itemRequest.getId());
        List<Item> items = new ArrayList<>();
        items.add(item);

        ItemRequestWithItems itemRequestWithItems = new ItemRequestWithItems(itemRequest, items);
        List<ItemRequestWithItems> itemRequestWithItemsList = new ArrayList<>();
        itemRequestWithItemsList.add(itemRequestWithItems);

        ItemRequestWithItemsDto itemRequestWithItemsDto = ItemRequestMapper.toItemRequestWithItemsDto(itemRequestWithItems);
        List<ItemRequestWithItemsDto> itemRequestWithItemsDtoList = new ArrayList<>();
        itemRequestWithItemsDtoList.add(itemRequestWithItemsDto);

        when(itemRequestService.findAllItemRequestsWithAllItemsForUser(anyLong())).thenReturn(itemRequestWithItemsList);

        mockMvc.perform(get("/requests")
                        .header(HttpProperties.xSharerUserId, requestor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].description", is(itemRequestWithItemsDtoList.get(0).getDescription())))
                .andExpect(jsonPath("$.[0].id", is(itemRequestWithItemsDtoList.get(0).getId()), Long.class))
                .andExpect(jsonPath("$.[0].created", is(createdString)))
                .andExpect(jsonPath("$.[0].items.[0].requestId", is(itemRequestWithItemsDtoList.get(0).getItems().get(0).getRequestId()), Long.class));

        verify(itemRequestService, times(1)).findAllItemRequestsWithAllItemsForUser(anyLong());
    }

    @Test
    public void testFindItemRequestByIdWithItemsForEach() throws Exception {
        LocalDateTime created = itemRequestDto.getCreated();
        String createdString = formatJson.format(created);

        User owner = new User();
        owner.setId(1L);
        owner.setName("Test owner name");
        owner.setEmail("Test owner email");

        Item item = new Item();
        item.setId(1L);
        item.setName("Test item name");
        item.setDescription("Test item description");
        item.setOwner(owner);
        item.setRequestId(itemRequest.getId());
        List<Item> items = new ArrayList<>();
        items.add(item);

        ItemRequestWithItems itemRequestWithItems = new ItemRequestWithItems(itemRequest, items);
        ItemRequestWithItemsDto itemRequestWithItemsDto = ItemRequestMapper.toItemRequestWithItemsDto(itemRequestWithItems);

        when(itemRequestService.findItemRequestByIdWithAllItems(anyLong())).thenReturn(itemRequestWithItems);

        mockMvc.perform(get("/requests/{requestId}", itemRequest.getId())
                        .header(HttpProperties.xSharerUserId, requestor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestWithItemsDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestWithItemsDto.getDescription())))
                .andExpect(jsonPath("$.created", is(createdString)))
                .andExpect(jsonPath("$.items.[0].requestId", is(itemRequestWithItemsDto.getItems().get(0).getRequestId()), Long.class))
                .andExpect(jsonPath("$.items.[0].id", is(itemRequestWithItemsDto.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.items.[0].name", is(itemRequestWithItemsDto.getItems().get(0).getName())))
                .andExpect(jsonPath("$.items.[0].description", is(itemRequestWithItemsDto.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$.items.[0].available", is(itemRequestWithItemsDto.getItems().get(0).getAvailable())));

        verify(itemRequestService, times(1)).findItemRequestByIdWithAllItems(anyLong());
    }

    @Test
    public void testGetAllOtherItemRequests() throws Exception {
        User anotherRequester = new User();
        anotherRequester.setId(2L);
        anotherRequester.setName("Test another name");
        anotherRequester.setEmail("Test another email");

        ItemRequest anotherItemRequest = new ItemRequest();
        anotherItemRequest.setId(2L);
        anotherItemRequest.setRequestor(anotherRequester);
        anotherItemRequest.setDescription("Test another description");
        anotherItemRequest.setCreated(LocalDateTime.now());

        ItemRequest otherItemRequest = new ItemRequest();
        otherItemRequest.setId(3L);
        otherItemRequest.setRequestor(anotherRequester);
        otherItemRequest.setDescription("Test other description");
        otherItemRequest.setCreated(LocalDateTime.now());

        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(anotherItemRequest);
        itemRequests.add(otherItemRequest);

        ItemRequestDto anotherItemRequestDto = ItemRequestMapper.toItemRequestDto(anotherItemRequest);
        ItemRequestDto otherItemRequestDto = ItemRequestMapper.toItemRequestDto(otherItemRequest);

        List<ItemRequestDto> otherItemRequestDtoList = new ArrayList<>();
        otherItemRequestDtoList.add(anotherItemRequestDto);
        otherItemRequestDtoList.add(otherItemRequestDto);

        when(itemRequestService.findAllOtherItemRequests(anyLong())).thenReturn(itemRequests);

        mockMvc.perform(get("/requests/all")
                        .header(HttpProperties.xSharerUserId, requestor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(otherItemRequestDtoList.get(0).getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(otherItemRequestDtoList.get(0).getDescription())))
                .andExpect(jsonPath("$.[0].requestor.id", is(otherItemRequestDtoList.get(0).getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$.[0].requestor.name", is(otherItemRequestDtoList.get(0).getRequestor().getName())))
                .andExpect(jsonPath("$.[0].requestor.email", is(otherItemRequestDtoList.get(0).getRequestor().getEmail())))
                .andExpect(jsonPath("$.[1].id", is(otherItemRequestDtoList.get(1).getId()), Long.class))
                .andExpect(jsonPath("$.[1].description", is(otherItemRequestDtoList.get(1).getDescription())))
                .andExpect(jsonPath("$.[1].requestor.id", is(otherItemRequestDtoList.get(1).getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$.[1].requestor.name", is(otherItemRequestDtoList.get(1).getRequestor().getName())))
                .andExpect(jsonPath("$.[1].requestor.email", is(otherItemRequestDtoList.get(1).getRequestor().getEmail())));

        verify(itemRequestService, times(1)).findAllOtherItemRequests(anyLong());
    }
}
