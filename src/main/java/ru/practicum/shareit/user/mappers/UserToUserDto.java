package ru.practicum.shareit.user.mappers;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

public class UserToUserDto {
    public static UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }
}
