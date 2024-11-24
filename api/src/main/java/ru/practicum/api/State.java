package ru.practicum.api;

import lombok.ToString;

import java.util.Optional;

@ToString
public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static Optional<State> from(String stateParam) {
        try {
            return Optional.of(State.valueOf(stateParam.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}

