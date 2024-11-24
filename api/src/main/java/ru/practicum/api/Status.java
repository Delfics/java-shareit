package ru.practicum.api;

import lombok.ToString;

@ToString
public enum Status {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED
}
