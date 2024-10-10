package ru.practicum.shareit.item.comment.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentDtoRequired;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.user.mappers.UserMapper;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {
    /*public CommentDto toDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setCreated(comment.getCreated());
        dto.setAuthor(UserMapper.toUserDto(comment.getUser()));
        dto.setItem(ItemMapper.toItemDto(comment.getItem()));
        return dto;
    }*/

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
