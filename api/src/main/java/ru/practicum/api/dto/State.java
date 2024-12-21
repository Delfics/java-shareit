package ru.practicum.api.dto;

import lombok.ToString;

@ToString
public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;
}

