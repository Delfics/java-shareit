package ru.practicum.gateway.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.practicum.api.dto.UserDto;
import ru.practicum.gateway.user.UserClient;
import ru.practicum.gateway.user.UserController;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class UserControllerTest {

    private MockMvc mockMvc;

    private final UserClient userClient = Mockito.mock(UserClient.class);

    @InjectMocks
    private UserController userController;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testCreateUser_Valid() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Test User");
        userDto.setEmail("testuser@example.com");

        when(userClient.create(any(UserDto.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("user created"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated());

        verify(userClient, times(1)).create(any(UserDto.class));
    }

    @Test
    public void testCreateUser_NotValid_BadRequest() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Test User");


        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).create(any(UserDto.class));
    }

    @Test
    public void testUpdateUser_Valid() throws Exception {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setName("Updated User");
        userDto.setEmail("updateduser@example.com");

        when(userClient.update(eq(userId), any(UserDto.class)))
                .thenReturn(ResponseEntity.ok().body("user updated"));

        mockMvc.perform(put("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("user updated"));

        verify(userClient, times(1)).update(eq(userId), any(UserDto.class));
    }

    @Test
    public void testUpdateUser_NotFound() throws Exception {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setName("Updated User");
        userDto.setEmail("testuser@example.com");

        when(userClient.update(any(Long.class), any(UserDto.class))).thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(put("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isNotFound());

        verify(userClient, times(1)).update(eq(userId), any(UserDto.class));
    }

    @Test
    public void testPatchUser_Valid() throws Exception {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setName("Patched User");
        userDto.setEmail("Patched user@example.com");

        when(userClient.patch(eq(userId), any(UserDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        verify(userClient, times(1)).patch(eq(userId), any(UserDto.class));
    }

    @Test
    public void testPatchUser_NotValid_BadRequest() throws Exception {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setName("Patched User");

        when(userClient.patch(eq(userId), any(UserDto.class)))
                .thenReturn(ResponseEntity.badRequest().build());

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, times(1)).patch(eq(userId), any(UserDto.class));
    }

    @Test
    public void testGetUserById() throws Exception {
        Long userId = 1L;

        when(userClient.getById(userId))
                .thenReturn(ResponseEntity.ok().body("user data"));

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("user data"));

        verify(userClient, times(1)).getById(userId);
    }

    @Test
    public void testGetUserById_NotFound() throws Exception {
        Long userId = 1L;

        when(userClient.getById(userId))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isNotFound());

        verify(userClient, times(1)).getById(userId);
    }

    @Test
    public void testDeleteUser() throws Exception {
        Long userId = 1L;

        when(userClient.delete(userId))
                .thenReturn(ResponseEntity.ok().body("user deleted"));

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("user deleted"));

        verify(userClient, times(1)).delete(userId);
    }

    @Test
    public void testDeleteUser_NotFound() throws Exception {
        Long userId = 1L;

        when(userClient.delete(userId))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNotFound());

        verify(userClient, times(1)).delete(userId);
    }
}
