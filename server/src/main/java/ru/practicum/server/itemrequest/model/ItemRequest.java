package ru.practicum.server.itemrequest.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.server.user.model.User;

import java.time.LocalDateTime;

@Table(name = "item_request")
@Entity
@Data
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "description")
    String description;

    @ManyToOne
    @JoinColumn(name = "users_id")
    User requestor;

    @Column(name = "created")
    LocalDateTime created;
}
