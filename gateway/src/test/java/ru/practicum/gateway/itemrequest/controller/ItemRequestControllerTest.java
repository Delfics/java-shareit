package ru.practicum.gateway.itemrequest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.api.dto.ItemRequestDto;
import ru.practicum.gateway.itemrequest.ItemRequestClient;
import ru.practicum.gateway.itemrequest.ItemRequestController;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class ItemRequestControllerTest {

    private final ItemRequestClient itemRequestClient = mock(ItemRequestClient.class);

    @InjectMocks
    private ItemRequestController itemRequestController;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemRequestController).build();
    }

    @Test
    void testFindAllItemRequestsWithItemsForEach() throws Exception {
        Long userId = 1L;
        when(itemRequestClient.findAllItemRequestsWithItemsForEach(userId)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemRequestClient).findAllItemRequestsWithItemsForEach(userId);
    }

    @Test
    void testFindItemRequestByIdWithItemsForEach() throws Exception {
        Long userId = 1L;
        Long requestId = 2L;

        when(itemRequestClient.findItemRequestByIdWithItemsForEach(userId, requestId))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemRequestClient).findItemRequestByIdWithItemsForEach(userId, requestId);
    }

    @Test
    void testGetAllOtherItemRequests() throws Exception {
        Long userId = 1L;

        when(itemRequestClient.getAllOtherItemRequests(userId)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemRequestClient).getAllOtherItemRequests(userId);
    }

    @Test
    void testCreateItemRequest() throws Exception {
        Long userId = 1L;
        ItemRequestDto itemRequestDto = new ItemRequestDto();  // Создаем объект ItemRequestDto
        itemRequestDto.setDescription("Test request");

        // Настроим mock поведение
        when(itemRequestClient.createItemRequest(any(), eq(userId)))
                .thenReturn(new ResponseEntity<>(HttpStatus.CREATED));

        mockMvc.perform(post("/requests")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDto))  // Преобразуем DTO в JSON
                        .header("X-Sharer-User-Id", userId))  // Заголовок с userId
                .andExpect(status().isCreated());  // Проверяем, что статус 201 CREATED

        // Проверим, что метод был вызван
        verify(itemRequestClient).createItemRequest(any(), eq(userId));
    }
}

