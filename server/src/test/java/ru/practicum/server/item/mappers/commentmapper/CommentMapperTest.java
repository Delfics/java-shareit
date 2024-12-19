package ru.practicum.server.item.mappers.commentmapper;

import org.junit.jupiter.api.Test;
import ru.practicum.api.dto.CommentDto;
import ru.practicum.api.dto.CommentDtoRequired;
import ru.practicum.server.item.comment.mappers.CommentMapper;
import ru.practicum.server.item.mappers.ItemMapper;
import ru.practicum.server.item.model.Comment;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.user.mappers.UserMapper;
import ru.practicum.server.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentMapperTest {

    @Test
    void testToComment() {
        CommentDto dto = new CommentDto();
        dto.setId(1L);
        dto.setText("This is a comment.");

        User author = new User();
        author.setId(2L);
        author.setName("John Doe");
        dto.setAuthor(UserMapper.toUserDto(author));

        Item item = new Item();
        item.setId(3L);
        item.setName("Item name");
        dto.setItem(ItemMapper.toItemDto(item));

        Comment comment = CommentMapper.toComment(dto);

        assertThat(comment).isNotNull();
        assertThat(comment.getId()).isEqualTo(dto.getId());
        assertThat(comment.getText()).isEqualTo(dto.getText());
        assertThat(comment.getUser()).isEqualTo(author);
        assertThat(comment.getItem()).isEqualTo(item);
        assertThat(comment.getCreated()).isNotNull();  // Проверяем, что время создания не null
    }

    @Test
    void testToDto() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("This is a comment.");

        User user = new User();
        user.setName("John Doe");
        comment.setUser(user);

        Item item = new Item();
        item.setName("Item name");
        comment.setItem(item);

        comment.setCreated(LocalDateTime.of(2024, 12, 15, 14, 30, 0));

        CommentDtoRequired dto = CommentMapper.toDto(comment);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(comment.getId());
        assertThat(dto.getText()).isEqualTo(comment.getText());
        assertThat(dto.getAuthorName()).isEqualTo(comment.getUser().getName());
        assertThat(dto.getCreated()).isEqualTo(comment.getCreated());
    }
}
