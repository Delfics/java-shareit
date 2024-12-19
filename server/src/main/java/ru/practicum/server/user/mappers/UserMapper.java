package ru.practicum.server.user.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.api.dto.UserDto;
import ru.practicum.server.user.model.User;

@UtilityClass
public class UserMapper {
    public User toUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }
}
