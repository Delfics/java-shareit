package ru.practicum.shareit.item.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.Item;

import java.util.List;

public interface CommentStorageJpa extends JpaRepository<Comment, Long> {

    @Query("SELECT comment " +
            "FROM Comment as comment " +
            "WHERE comment.item IN ?1 " +
            "ORDER BY comment.created DESC")
    List<Comment> findCommentsByItemsSortedByDate(List<Item> items);

    @Query("SELECT с " +
            "FROM Comment as с " +
            "WHERE с.item.id = ?1")
    List<Comment> findCommentByItemId(Long itemId);
}
