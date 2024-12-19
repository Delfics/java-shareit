package ru.practicum.server.user.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.practicum.api.dto.UserDto;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.service.UserService;
import ru.practicum.server.user.mappers.UserMapper;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
    @Qualifier("userServiceImplJpa")
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<UserDto> getAll() {
        log.info("Запрос Get - getAll");
        return userService.getAll().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        log.info("Запрос Get - getById. Входные параметры id {}", id);
        User userById = userService.getById(id);
        return UserMapper.toUserDto(userById);
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("Запрос Post - create. Входные параметры userDto {}", userDto.toString());
        User user = UserMapper.toUser(userDto);
        User createdUser = userService.create(user);
        return UserMapper.toUserDto(createdUser);
    }

    @PutMapping("/{userId}")
    public UserDto update(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("Запрос Put - update. Входные параметры userId {}, userDto {}", userId, userDto.toString());
        User user = UserMapper.toUser(userDto);
        User updatedUser = userService.update(user, userId);
        return UserMapper.toUserDto(updatedUser);
    }

    @PatchMapping("/{userId}")
    public UserDto patch(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("Запрос Patch - patch. Входные параметры userId {}, userDto {}", userId, userDto.toString());
        return UserMapper.toUserDto(userService.patch(UserMapper.toUser(userDto), userId));
    }


    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable Long userId) {
        log.info("Запрос Delete - deleteById. Входные параметры userId {}", userId);
        userService.deleteById(userId);
    }
}
