package ru.practicum.gateway.user.controller;

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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import ru.practicum.api.dto.UserDto;
import ru.practicum.gateway.Application;
import ru.practicum.gateway.config.TestConfiguration;
import ru.practicum.gateway.user.UserController;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = {Application.class, TestConfiguration.class})
public class UserControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private UserController userController;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testGetItems() throws Exception {
        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class))).thenReturn(ResponseEntity.status(HttpStatus.OK).build());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateUser_Valid() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Test User");
        userDto.setEmail("testuser@example.com");

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class))).thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(userDto));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    public void testCreateUser_NotValid_BadRequest() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Test User");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateUser_Valid() throws Exception {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setName("Updated User");
        userDto.setEmail("updateduser@example.com");

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class))).thenReturn(ResponseEntity.status(HttpStatus.OK).body(userDto));

        mockMvc.perform(put("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    public void testUpdateUser_NotFound() throws Exception {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setName("Updated User");
        userDto.setEmail("testuser@example.com");

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class))).thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

        mockMvc.perform(put("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testPatchUser_Valid() throws Exception {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setName("Patched User");
        userDto.setEmail("Patched user@example.com");

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class))).thenReturn(ResponseEntity.status(HttpStatus.OK).body(userDto));

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    public void testPatchUser_NotValid_BadRequest() throws Exception {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setName("Patched User");

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class))).thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetUserById() throws Exception {
        Long userId = 1L;

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class))).thenReturn(ResponseEntity.status(HttpStatus.OK).body("user data"));

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("user data"));
    }

    @Test
    public void testGetUserById_NotFound() throws Exception {
        Long userId = 1L;

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class))).thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteUser() throws Exception {
        Long userId = 1L;

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class))).thenReturn(ResponseEntity.status(HttpStatus.OK).build());

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteUser_NotFound() throws Exception {
        Long userId = 1L;

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class),
                eq(Object.class))).thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNotFound());
    }
}
