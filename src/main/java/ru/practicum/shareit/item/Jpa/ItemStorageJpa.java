package ru.practicum.shareit.item.Jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.comment.Comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


public interface ItemStorageJpa extends JpaRepository<Item, Long> {

    @Query("SELECT it " +
            "FROM Item as it " +
            "WHERE it.owner.id = ?1")
    List<Item> findItemsByOwnerId(Long Id);

    @Query("SELECT book.item as book_item," +
            "MIN(CASE WHEN book.start > ?1 THEN book.start END) as next_booking_time," +
            "MAX(book.end) as last_time " +
            "FROM Booking as book " +
            "WHERE book.item.owner.id = ?2 " +
            "GROUP BY book.item")
    List<Object[]> findWithLastAndCloserBookingByOwnerId(LocalDateTime now, Long ownerId);

    @Query("SELECT  it " +
            "FROM Item as it " +
            "WHERE (it.name ILIKE CONCAT('%', ?1, '%') " +
            "OR it.description ILIKE CONCAT ('%', ?1, '%')) " +
            "AND it.available = true")
    List<Item> findItemsByText(String text);
}
