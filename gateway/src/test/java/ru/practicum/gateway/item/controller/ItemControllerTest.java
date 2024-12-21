package ru.practicum.gateway.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import ru.practicum.api.dto.CommentDto;
import ru.practicum.api.dto.ItemDto;
import ru.practicum.api.dto.UserDto;
import ru.practicum.gateway.Application;
import ru.practicum.gateway.config.TestConfiguration;
import ru.practicum.gateway.item.ItemController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = {Application.class, TestConfiguration.class})
public class ItemControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ItemController itemController;

    @Autowired
    private ObjectMapper objectMapper;

    private final DateTimeFormatter formatJson = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
    }

    @Test
    public void testCreateItem_Valid() throws Exception {
        Long userId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");
        itemDto.setDescription("Description of the test item");
        itemDto.setAvailable(true);

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class))).thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(itemDto));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    public void testCreateItem_NotValid() throws Exception {
        Long userId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");
        itemDto.setDescription("Description of the test item");

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void testGetItems() throws Exception {
        Long userId = 1L;
        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class))).thenReturn(ResponseEntity.status(HttpStatus.OK).build());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetItems_NotFound() throws Exception {
        Long userId = 1L;

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class))).thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetByIdItemWithComments() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class))).thenReturn(ResponseEntity.status(HttpStatus.OK).build());

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateComment() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Author");
        userDto.setEmail("Author@email.com");

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");
        itemDto.setDescription("Description of the test item");
        itemDto.setAvailable(true);

        Long userId = 1L;
        Long itemId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Great item!");
        commentDto.setAuthor(userDto);
        commentDto.setCreated(LocalDateTime.now());
        commentDto.setItem(itemDto);

        String created = commentDto.getCreated().format(formatJson);

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class))).thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(commentDto));

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.created", is(created)))
                .andExpect(jsonPath("$.author.id", is(commentDto.getAuthor().getId()), Long.class))
                .andExpect(jsonPath("$.author.name", is(commentDto.getAuthor().getName())))
                .andExpect(jsonPath("$.author.email", is(commentDto.getAuthor().getEmail())))
                .andExpect(jsonPath("$.item.id", is(commentDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(commentDto.getItem().getName())))
                .andExpect(jsonPath("$.item.description", is(commentDto.getItem().getDescription())))
                .andExpect(jsonPath("$.item.available", is(commentDto.getItem().getAvailable())));
    }

    @Test
    public void testCreateComment_BadRequest() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class))).thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPatchItem() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Item");
        itemDto.setDescription("Updated description");
        itemDto.setAvailable(true);

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class))).thenReturn(ResponseEntity.status(HttpStatus.OK).build());

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk());
    }

    @Test
    public void testPatchItem_NotValid_BadRequest() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Item");
        itemDto.setDescription("Updated description");

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class))).thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSearchItems() throws Exception {
        String searchText = "Test Item";
        Map<String, Object> parameter = Map.of("text", searchText);
        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class), eq(parameter))).thenReturn(ResponseEntity.status(HttpStatus.OK).build());

        mockMvc.perform(get("/items/search")
                        .param("text", searchText))
                .andExpect(status().isOk());

    }
}
