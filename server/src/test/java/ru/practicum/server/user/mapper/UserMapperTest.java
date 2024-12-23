package ru.practicum.server.user.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.api.dto.UserDto;
import ru.practicum.server.user.mappers.UserMapper;
import ru.practicum.server.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

public class UserMapperTest {

    @Test
    void testToUser() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("John");
        userDto.setEmail("john@example.com");

        User user = UserMapper.toUser(userDto);

        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(userDto.getId());
        assertThat(user.getName()).isEqualTo(userDto.getName());
        assertThat(user.getEmail()).isEqualTo(userDto.getEmail());
    }

    @Test
    void testToUserDto() {
        User user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@example.com");

        UserDto userDto = UserMapper.toUserDto(user);

        assertThat(userDto).isNotNull();
        assertThat(userDto.getId()).isEqualTo(user.getId());
        assertThat(userDto.getName()).isEqualTo(user.getName());
        assertThat(userDto.getEmail()).isEqualTo(user.getEmail());
    }
}
