package ru.practicum.api.dto;

import lombok.ToString;

@ToString
public enum Status {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED
}
