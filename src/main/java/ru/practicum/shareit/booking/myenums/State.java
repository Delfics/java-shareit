package ru.practicum.shareit.booking.myenums;

import lombok.ToString;

@ToString
public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED
}

