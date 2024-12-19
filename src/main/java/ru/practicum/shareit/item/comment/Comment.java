package ru.practicum.shareit.item.comment;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "text")
    String text;

    @Column(name = "created_time")
    LocalDateTime created;

    @JoinColumn(name = "items_id")
    @ManyToOne(fetch = FetchType.LAZY)
    Item item;

    @JoinColumn(name = "users_id")
    @ManyToOne(fetch = FetchType.LAZY)
    User user;
}
