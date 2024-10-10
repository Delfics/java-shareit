package ru.practicum.shareit.user.Jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.User;

public interface UserStorageJpa extends JpaRepository<User, Long> {

    @Query("SELECT COUNT(user) > 0 " +
            "FROM User as user " +
            "WHERE user.email = ?1")
    Boolean existsByEmail(String email);
}
