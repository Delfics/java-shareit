package ru.practicum.server.item.controller;

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
import ru.practicum.api.dto.CommentDto;
import ru.practicum.api.dto.CommentDtoRequired;
import ru.practicum.api.dto.ItemDto;
import ru.practicum.api.dto.ItemWithBookingsAndCommentsDto;
import ru.practicum.server.item.comment.mappers.CommentMapper;
import ru.practicum.server.item.mappers.ItemMapper;
import ru.practicum.server.item.model.Comment;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.model.ItemWithBookingsAndComments;
import ru.practicum.server.item.service.ItemServiceImplJpa;
import ru.practicum.server.user.mappers.UserMapper;
import ru.practicum.server.user.model.User;
import ru.practicum.server.utils.HttpProperties;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class ItemControllerTest {
    @InjectMocks
    private ItemController itemController;
    private final ItemServiceImplJpa itemServiceImplJpa = Mockito.mock(ItemServiceImplJpa.class);
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private Item createdItem;
    private ItemDto itemDto;
    private User owner;
    private Comment comment;
    private CommentDtoRequired createdCommentDto;
    private User commentator;
    private final DateTimeFormatter formatJson = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();

        owner = new User();
        owner.setId(1L);
        owner.setName("Test Owner");
        owner.setEmail("test@test.com");

        createdItem = new Item();
        createdItem.setId(1L);
        createdItem.setOwner(owner);
        createdItem.setAvailable(true);
        createdItem.setName("Test Item");
        createdItem.setDescription("Test Item");

        itemDto = ItemMapper.toItemDto(createdItem);

        commentator = new User();
        commentator.setId(2L);
        commentator.setName("Test Commentator");
        commentator.setEmail("test@Comenntatortest.com");

        comment = new Comment();
        comment.setId(1L);
        comment.setText("Test Comment");
        comment.setCreated(LocalDateTime.now());
        comment.setItem(createdItem);
        comment.setUser(commentator);

        createdCommentDto = CommentMapper.toDto(comment);
    }

    @Test
    public void testCreateItem() throws Exception {
        when(itemServiceImplJpa.createItem(any(Item.class), anyLong())).thenReturn(createdItem);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpProperties.xSharerUserId, owner.getId()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        verify(itemServiceImplJpa, times(1)).createItem(any(Item.class), anyLong());
    }

    @Test
    public void testCreatComment() throws Exception {
        String created = createdCommentDto.getCreated().format(formatJson);

        CommentDto commentDto = new CommentDto();
        commentDto.setId(createdCommentDto.getId());
        commentDto.setText(createdCommentDto.getText());
        commentDto.setCreated(createdCommentDto.getCreated());
        commentDto.setItem(itemDto);
        commentDto.setAuthor(UserMapper.toUserDto(commentator));

        when(itemServiceImplJpa.createComment(any(Comment.class), anyLong(), anyLong())).thenReturn(comment);

        when(itemServiceImplJpa.addAuthorToCommentDto(any(CommentDto.class), anyLong())).thenReturn(commentDto);
        when(itemServiceImplJpa.addItemToCommentDto(any(CommentDto.class), anyLong())).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", createdItem.getId())
                        .content(objectMapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpProperties.xSharerUserId, commentator.getId()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthor().getName())))
                .andExpect(jsonPath("$.created", is(created)));

        verify(itemServiceImplJpa, times(1)).createComment(any(Comment.class), anyLong(), anyLong());
    }

    @Test
    public void testPatchItem() throws Exception {
        createdItem.setName("Test patched Item name");
        createdItem.setDescription("Test patched Item description");

        itemDto = ItemMapper.toItemDto(createdItem);

        when(itemServiceImplJpa.patch(anyLong(), any(Item.class), anyLong())).thenReturn(createdItem);

        mockMvc.perform(patch("/items/{itemId}", createdItem.getId())
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpProperties.xSharerUserId, owner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        verify(itemServiceImplJpa, times(1)).patch(anyLong(), any(Item.class), anyLong());
    }

    @Test
    public void testGetByIdItemWithComments() throws Exception {
        List<String> listOfComments = new ArrayList<>();
        listOfComments.add(comment.getText());

        ItemWithBookingsAndComments itemWithBookingsAndComments = new ItemWithBookingsAndComments();
        itemWithBookingsAndComments.setItem(createdItem);
        itemWithBookingsAndComments.setComments(listOfComments);
        ItemWithBookingsAndCommentsDto itemWithBookingsAndCommentsDto = ItemMapper.toItemWithBookingsAndCommentsDto(itemWithBookingsAndComments);

        when(itemServiceImplJpa.findItemWithComments(anyLong())).thenReturn(itemWithBookingsAndComments);

        mockMvc.perform(get("/items/{itemId}", createdItem.getId())
                        .header(HttpProperties.xSharerUserId, anyLong()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemWithBookingsAndComments.getItem().getName())))
                .andExpect(jsonPath("$.description", is(itemWithBookingsAndComments.getItem().getDescription())))
                .andExpect(jsonPath("$.available", is(itemWithBookingsAndComments.getItem().getAvailable())))
                .andExpect(jsonPath("$.comments", is(itemWithBookingsAndComments.getComments())));

        verify(itemServiceImplJpa, times(1)).findItemWithComments(anyLong());
    }

    @Test
    public void testGetItems() throws Exception {
        LocalDateTime nextBookingTime = LocalDateTime.now();
        LocalDateTime lastBookingTime = LocalDateTime.now().plusDays(1);
        String newNextBookingTime = nextBookingTime.format(formatJson);
        String newLastBookingTime = lastBookingTime.format(formatJson);
        List<String> listOfComments = new ArrayList<>();
        listOfComments.add(comment.getText());

        List<ItemWithBookingsAndComments> itemsWithBookingsAndComments = new ArrayList<>();
        ItemWithBookingsAndComments itemWithBookingsAndComments = new ItemWithBookingsAndComments();
        itemWithBookingsAndComments.setItem(createdItem);
        itemWithBookingsAndComments.setComments(listOfComments);
        itemWithBookingsAndComments.setNextBookingTime(nextBookingTime);
        itemWithBookingsAndComments.setLastBookingTime(lastBookingTime);
        itemsWithBookingsAndComments.add(itemWithBookingsAndComments);

        List<ItemWithBookingsAndCommentsDto> itemWithBookingsAndCommentsDtos = new ArrayList<>();
        ItemWithBookingsAndCommentsDto itemWithBookingsAndCommentsDto = ItemMapper.toItemWithBookingsAndCommentsDto(itemWithBookingsAndComments);
        itemWithBookingsAndCommentsDtos.add(itemWithBookingsAndCommentsDto);

        when(itemServiceImplJpa.findItemsWithCommentsBookingByUserId(anyLong())).thenReturn(itemsWithBookingsAndComments);

        mockMvc.perform(get("/items")
                        .header(HttpProperties.xSharerUserId, anyLong()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(itemWithBookingsAndCommentsDtos.get(0).getName())))
                .andExpect(jsonPath("$[0].description", is(itemWithBookingsAndCommentsDtos.get(0).getDescription())))
                .andExpect(jsonPath("$[0].nextBooking", is(newNextBookingTime)))
                .andExpect(jsonPath("$[0].lastBooking", is(newLastBookingTime)))
                .andExpect(jsonPath("$[0].available", is(itemWithBookingsAndCommentsDtos.get(0).getAvailable())))
                .andExpect(jsonPath("$[0].comments", is(itemWithBookingsAndCommentsDtos.get(0).getComments())));

        verify(itemServiceImplJpa, times(1)).findItemsWithCommentsBookingByUserId(anyLong());

    }

    @Test
    public void testSearchItems() throws Exception {
        createdItem.setDescription("test");
        itemDto.setDescription("test");
        List<Item> items = List.of(createdItem);
        List<ItemDto> itemDtos = List.of(itemDto);

        when(itemServiceImplJpa.findItemsByText(anyString())).thenReturn(items);

        mockMvc.perform(get("/items/search?text=test")
                        .header(HttpProperties.xSharerUserId, anyLong()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description", is(itemDtos.get(0).getDescription())));

        verify(itemServiceImplJpa, times(1)).findItemsByText(anyString());
    }
}
