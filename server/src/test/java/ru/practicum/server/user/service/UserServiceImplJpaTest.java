package ru.practicum.server.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.exception.ConflictException;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.exception.ValidationException;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserStorageJpa;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceImplJpaTest {

    @Autowired
    private UserServiceImplJpa userService;

    @Autowired
    private UserStorageJpa userStorageJpa;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setName("Test User");
        user.setEmail("testuser@example.com");
    }

    @Test
    public void testCreateUser() {
        User savedUser = userService.create(user);

        assertNotNull(savedUser);
        assertEquals("Test User", savedUser.getName(), "Имя соответствует");
        assertEquals("testuser@example.com", savedUser.getEmail(), "Email тоже");
    }

    @Test
    public void testGetAllUsers() {
        User user1 = new User();
        user1.setName("User1");
        user1.setEmail("User1@example.com");

        User user2 = new User();
        user2.setName("User2");
        user2.setEmail("User2@example.com");


        userService.create(user1);
        userService.create(user2);

        List<User> users = userService.getAll();

        assertNotNull(users);
        assertTrue(users.size() >= 2);
    }

    @Test
    public void testGetUserById() {
        User savedUser = userStorageJpa.save(user);

        User foundUser = userService.getById(savedUser.getId());

        assertNotNull(foundUser, "Пользователь существует");
        assertEquals(savedUser.getId(), foundUser.getId(), "Id совпадает");
        assertEquals(savedUser.getName(), foundUser.getName(), "Name совпадает");
        assertEquals(savedUser.getEmail(), foundUser.getEmail(), "Email совпадает");
    }

    @Test
    public void testUserNotFound() {
        assertThrows(NotFoundException.class, () -> userService.getById(999L), "Пытаемся получить пользователя с несуществующим ID");
    }

    @Test
    public void testUpdateUser() {
        userService.create(user);

        String newEmail = "User2@example.com";
        String newName = "User2";
        User savedUser = new User();

        savedUser.setEmail(newEmail);
        savedUser.setName(newName);

        userService.update(savedUser, user.getId());

        assertNotEquals(savedUser, user, "User не совпадают");
        assertEquals(newEmail, savedUser.getEmail(), "Email изменился");
        assertEquals(newName, savedUser.getName(), "Name изменился");
    }

    @Test
    public void testPatchUser() {
        String patchName = "Patched name";
        String patchEmail = "Patched email221@example.com";

        User created = userService.create(user);

        User user1 = new User();
        user1.setName(patchName);
        user1.setEmail(patchEmail);

        User patch = userService.patch(user1, created.getId());

        assertEquals(patchName, patch.getName(), "Name изменено");
        assertEquals(patchEmail, patch.getEmail(), "Email изменен");

    }

    @Test
    public void testDeleteUser() {
        userService.create(user);
        userService.deleteById(user.getId());

        assertEquals(Optional.empty(), userStorageJpa.findById(user.getId()), "Пользователь удалён");
    }

    @Test
    public void testPatchUserNameNullValidationException() {
        user.setName(null);
        userStorageJpa.save(user);

        User newUser = new User();
        newUser.setId(user.getId());
        newUser.setName(null);
        newUser.setEmail("User2@example.com");

        assertThrows(ValidationException.class, () -> userService.patch(newUser, user.getId()), "Throw ValidationException");
    }

    @Test
    public void testPatchUserEmailExistConflictException() {
        user.setName("newName");
        userStorageJpa.save(user);

        User newUser = new User();
        newUser.setId(user.getId());
        newUser.setName(null);
        newUser.setEmail("testuser@example.com");

        assertThrows(ConflictException.class, () -> userService.patch(newUser, user.getId()), "Throw ValidationException");
    }

    @Test
    public void testPatchUserEmailNullValidationException() {
        user.setName("name");
        user.setEmail(null);
        userStorageJpa.save(user);

        User newUser = new User();
        newUser.setId(user.getId());
        newUser.setName("another");
        newUser.setEmail(null);

        assertThrows(ValidationException.class, () -> userService.patch(newUser, user.getId()), "Throw ValidationException");
    }

    @Test
    public void testPatchUserEmailConflictException() {
        userStorageJpa.save(user);

        User user1 = new User();
        user1.setName("Baby");
        user1.setEmail("testuser95@example.com");

        userService.create(user1);

        user.setEmail(user1.getEmail());

        assertThrows(ConflictException.class, () -> userService.patch(user, user.getId()), "Throw ConflictException");
    }

    @Test
    public void testDeleteUserThrowNotFoundException() {
        user.setEmail("anotheruser@example.com");
        userService.create(user);

        userService.deleteById(user.getId());

        assertThrows(NotFoundException.class, () -> userService.getById(user.getId()), "Throw NotFoundException");
    }

    @Test
    public void testValidationExceptionWithNullOrEmptyEmailOrWithOutSymbolDog() {
        User userEmailNull = new User();
        userEmailNull.setId(2L);
        userEmailNull.setName("User1");

        assertThrows(ValidationException.class, () -> userService.create(userEmailNull), "User email = null");

        User userEmailEmpty = new User();
        userEmailEmpty.setId(2L);
        userEmailEmpty.setName("User2");
        userEmailEmpty.setEmail("");

        assertThrows(ValidationException.class, () -> userService.create(userEmailEmpty), "User имеет пустой email");

        User userEmailWithOutDog = new User();
        userEmailWithOutDog.setId(2L);
        userEmailWithOutDog.setName("User2");
        userEmailWithOutDog.setEmail("user2example.com");

        assertThrows(ValidationException.class, () -> userService.create(userEmailWithOutDog), "User без @");
    }

    @Test
    public void testConflictExceptionWhenEmailAlreadyExists() {
        userStorageJpa.save(user);

        User invalidUserEmail = new User();
        invalidUserEmail.setName("User1");
        invalidUserEmail.setEmail("testuser@example.com");

        assertThrows(ConflictException.class, () -> userService.create(invalidUserEmail), "Email существует и летит исключение");
    }

    @Test
    public void testBooleanExistsWhenEmailAlreadyExists() {
        userStorageJpa.save(user);

        User invalidUserEmail = new User();
        invalidUserEmail.setName("User1");
        invalidUserEmail.setEmail("testuser@example.com");

        Boolean thisTrue = true;

        assertEquals(thisTrue, userService.existsByEmail(invalidUserEmail.getEmail()), "Такой email уже существует поэтому true");
    }
}
