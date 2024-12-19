package ru.practicum.gateway.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.api.dto.CommentDto;
import ru.practicum.api.dto.ItemDto;
import ru.practicum.gateway.item.ItemClient;
import ru.practicum.gateway.item.ItemController;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class ItemControllerTest {

    private MockMvc mockMvc;

    private final ItemClient itemClient = Mockito.mock(ItemClient.class);

    @InjectMocks
    private ItemController itemController;

    @Autowired
    private ObjectMapper objectMapper;

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

        when(itemClient.createItem(eq(userId), any(ItemDto.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(itemDto));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated());

        verify(itemClient).createItem(eq(userId), any(ItemDto.class));
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

        verify(itemClient, never()).createItem(eq(userId), any(ItemDto.class));
    }

    @Test
    public void testGetItems() throws Exception {
        Long userId = 1L;
        when(itemClient.getAll(any(Long.class))).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).getAll(any(Long.class));
    }

    @Test
    public void testGetItems_NotFound() throws Exception {
        Long userId = 1L;
        when(itemClient.getAll(any(Long.class))).thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound());

        verify(itemClient, times(1)).getAll(any(Long.class));
    }

    @Test
    public void testGetByIdItemWithComments() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        when(itemClient.getByIdItemWithComments(eq(itemId), eq(userId)))
                .thenReturn(ResponseEntity.ok().body("item with comments"));

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("item with comments"));

        verify(itemClient, times(1)).getByIdItemWithComments(eq(itemId), eq(userId));
    }

    @Test
    public void testCreateComment() throws Exception {
        // Arrange
        Long userId = 1L;
        Long itemId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        when(itemClient.createComment(eq(commentDto), eq(itemId), eq(userId)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("comment created"));

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("comment created"));

        verify(itemClient, times(1)).createComment(eq(commentDto), eq(itemId), eq(userId));
    }

    @Test
    public void testCreateComment_BadRequest() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        when(itemClient.createComment(eq(commentDto), eq(itemId), eq(userId)))
                .thenReturn(ResponseEntity.badRequest().build());

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, times(1)).createComment(eq(commentDto), eq(itemId), eq(userId));
    }

    @Test
    public void testPatchItem() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Item");
        itemDto.setDescription("Updated description");
        itemDto.setAvailable(true);

        when(itemClient.patchItem(eq(itemId), any(ItemDto.class), eq(userId)))
                .thenReturn(ResponseEntity.ok().body("item updated"));

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).patchItem(eq(itemId), any(ItemDto.class), eq(userId));
    }

    @Test
    public void testPatchItem_NotValid_BadRequest() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Item");
        itemDto.setDescription("Updated description");

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).patchItem(any(Long.class), any(ItemDto.class), any(Long.class));
    }

    @Test
    public void testSearchItems() throws Exception {
        // Arrange
        String searchText = "Test Item";
        when(itemClient.searchItems(eq(searchText)))
                .thenReturn(ResponseEntity.ok().body("search results"));

        // Act & Assert
        mockMvc.perform(get("/items/search")
                        .param("text", searchText))
                .andExpect(status().isOk())
                .andExpect(content().string("search results"));

        verify(itemClient, times(1)).searchItems(eq(searchText));
    }
}
