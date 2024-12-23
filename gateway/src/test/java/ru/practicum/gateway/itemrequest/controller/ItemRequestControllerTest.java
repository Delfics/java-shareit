package ru.practicum.gateway.itemrequest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import ru.practicum.api.dto.ItemRequestDto;
import ru.practicum.gateway.Application;
import ru.practicum.gateway.config.TestConfiguration;
import ru.practicum.gateway.itemrequest.ItemRequestController;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = {Application.class, TestConfiguration.class})
public class ItemRequestControllerTest {

    @Autowired
    private ItemRequestController itemRequestController;

    private MockMvc mockMvc;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemRequestController).build();
    }

    @Test
    void testFindAllItemRequestsWithItemsForEach() throws Exception {
        Long userId = 1L;
        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class))).thenReturn(ResponseEntity.status(HttpStatus.OK).build());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }

    @Test
    void testFindItemRequestByIdWithItemsForEach() throws Exception {
        Long userId = 1L;
        Long requestId = 2L;

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class))).thenReturn(ResponseEntity.status(HttpStatus.OK).build());

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

    }

    @Test
    void testGetAllOtherItemRequests() throws Exception {
        Long userId = 1L;

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class))).thenReturn(ResponseEntity.status(HttpStatus.OK).build());

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateItemRequest() throws Exception {
        Long userId = 1L;
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Test request");

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class))).thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(itemRequestDto));

        mockMvc.perform(post("/requests")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }
}

