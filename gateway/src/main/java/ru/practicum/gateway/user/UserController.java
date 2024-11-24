package ru.practicum.gateway.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.api.UserDto;

@Slf4j
@RequestMapping(path = "/users")
@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;


    @GetMapping()
    public ResponseEntity<Object> getAll() {
        log.info("Get users ");
        return userClient.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id) {
        log.info("Get - getById. Входные параметры id {}", id);
        return userClient.getById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid UserDto userDto) {
        log.info("Creating user={}", userDto);
        return userClient.create(userDto);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("Updating user id={}, user={}",userId, userDto);
        return userClient.update(userId, userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> patch(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("Patch  userId {}, userDto {}", userId, userDto);
        return userClient.patch(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable Long userId) {
        log.info("Deleting user id={}", userId);
        return userClient.delete(userId);
    }
}
