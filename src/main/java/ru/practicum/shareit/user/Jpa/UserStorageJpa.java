package ru.practicum.shareit.user.Jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.User;

public interface UserStorageJpa extends JpaRepository<User, Long> {
}
