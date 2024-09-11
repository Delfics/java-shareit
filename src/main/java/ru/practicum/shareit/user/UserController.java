package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.mappers.UserDtoToUser;
import ru.practicum.shareit.user.mappers.UserToUserDto;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<UserDto> getAll() {
        return userService.getAll().stream()
                .map(UserToUserDto::toUserDto)
                .toList();

    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Integer id) {
        User userById = userService.getById(id);
        return UserToUserDto.toUserDto(userById);
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        User user = UserDtoToUser.toUser(userDto);
        User createdUser = userService.create(user);
        return UserToUserDto.toUserDto(createdUser);
    }

    @PutMapping("/{userId}")
    public UserDto update(@PathVariable Integer userId, @RequestBody UserDto userDto) {
        User user = UserDtoToUser.toUser(userDto);
        User updatedUser = userService.update(user, userId);
        return UserToUserDto.toUserDto(updatedUser);
    }

    @PatchMapping("/{userId}")
    public UserDto patch(@PathVariable Integer userId, @RequestBody UserDto userDto) {
        return UserToUserDto.toUserDto(userService.patch(UserDtoToUser.toUser(userDto), userId));
    }


    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable Integer userId) {
        userService.delete(userId);
    }
}
