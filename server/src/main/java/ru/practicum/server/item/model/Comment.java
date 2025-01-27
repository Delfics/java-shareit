package ru.practicum.server.item.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.server.user.model.User;

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
