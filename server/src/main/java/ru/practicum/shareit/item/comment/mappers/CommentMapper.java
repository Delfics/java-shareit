package ru.practicum.shareit.item.comment.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.dto.CommentDto;
import ru.practicum.shareit.dto.CommentDtoRequired;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.mappers.UserMapper;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {

    public Comment toComment(CommentDto dto) {
        Comment comment = new Comment();
        comment.setId(dto.getId());
        comment.setText(dto.getText());
        comment.setCreated(LocalDateTime.now());
        comment.setUser(UserMapper.toUser(dto.getAuthor()));
        comment.setItem(ItemMapper.toItem(dto.getItem()));
        return comment;
    }

    public CommentDtoRequired toDto(Comment comment) {
        CommentDtoRequired dto = new CommentDtoRequired();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setAuthorName(comment.getUser().getName());
        dto.setCreated(comment.getCreated());
        return dto;
    }
}
