package ru.practicum.server.user.controller;

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
import ru.practicum.api.dto.UserDto;
import ru.practicum.server.user.mappers.UserMapper;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.service.UserService;
import ru.practicum.server.user.service.UserServiceImplJpa;

import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @InjectMocks
    private UserController userController;
    private MockMvc mockMvc;
    private final UserService userService = Mockito.mock(UserServiceImplJpa.class);
    @Autowired
    private ObjectMapper objectMapper;
    private UserDto userDto;
    private User createdUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        createdUser = new User();
        createdUser.setId(1L);
        createdUser.setName("Test User");
        createdUser.setEmail("Test@gmail.com");

        userDto = UserMapper.toUserDto(createdUser);
    }

    @Test
    public void testCreateUser() throws Exception {
        when(userService.create(any(User.class))).thenReturn(createdUser);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(this.userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(this.userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(this.userDto.getName())))
                .andExpect(jsonPath("$.email", is(this.userDto.getEmail())));

        verify(userService, times(1)).create(any(User.class));
    }

    @Test
    public void testGetAllUsers() throws Exception {
        User newUser = new User();
        newUser.setId(2L);
        newUser.setName("Test NewUser");
        newUser.setEmail("NewUser@gmail.com");

        UserDto newUserDto = UserMapper.toUserDto(newUser);
        Collection<User> getAllUsers = new ArrayList<>();
        getAllUsers.add(createdUser);
        getAllUsers.add(newUser);

        when(userService.getAll()).thenReturn(getAllUsers);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(this.userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(this.userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(this.userDto.getEmail())))
                .andExpect(jsonPath("$[1].id", is(newUserDto.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(newUserDto.getName())))
                .andExpect(jsonPath("$[1].email", is(newUserDto.getEmail())));

        verify(userService, times(1)).getAll();
    }

    @Test
    public void testGetByIdUser() throws Exception {
        Long createdUserId = createdUser.getId();
        when(userService.getById(createdUserId)).thenReturn(createdUser);

        mockMvc.perform(get("/users/{id}", createdUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(this.userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(this.userDto.getName())))
                .andExpect(jsonPath("$.email", is(this.userDto.getEmail())));

        verify(userService, times(1)).getById(anyLong());
    }

    @Test
    public void testUpdateUser() throws Exception {
        User updatedUser = new User();
        updatedUser.setId(createdUser.getId());
        updatedUser.setName("updatedName");
        updatedUser.setEmail("updatedEmail");

        UserDto updatedUserDto = UserMapper.toUserDto(updatedUser);

        when(userService.update(updatedUser, updatedUser.getId())).thenReturn(updatedUser);

        mockMvc.perform(put("/users/{userId}", createdUser.getId())
                        .content(objectMapper.writeValueAsString(updatedUserDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedUserDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updatedUserDto.getName())))
                .andExpect(jsonPath("$.email", is(updatedUserDto.getEmail())));

        verify(userService, times(1)).update(updatedUser, updatedUser.getId());
    }

    @Test
    public void testPatchUser() throws Exception {
        User patchedUser = new User();
        patchedUser.setId(createdUser.getId());
        patchedUser.setName("patchedName");
        patchedUser.setEmail(createdUser.getEmail());

        UserDto patchedUserDto = UserMapper.toUserDto(patchedUser);

        when(userService.patch(patchedUser, patchedUser.getId())).thenReturn(patchedUser);

        mockMvc.perform(patch("/users/{userId}", patchedUser.getId())
                        .content(objectMapper.writeValueAsString(patchedUserDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(patchedUserDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(patchedUserDto.getName())))
                .andExpect(jsonPath("$.email", is(patchedUserDto.getEmail())));

        verify(userService, times(1)).patch(patchedUser, patchedUser.getId());
    }

    @Test
    public void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteById(createdUser.getId());

        mockMvc.perform(delete("/users/{id}", createdUser.getId()))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteById(createdUser.getId());
    }
}
