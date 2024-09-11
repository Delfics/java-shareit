package ru.practicum.shareit.user.mappers;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

public class UserDtoToUser {
    public static User toUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }
}
