package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;


public interface ItemStorageJpa extends JpaRepository<Item, Long> {

    @Query("SELECT it " +
            "FROM Item as it " +
            "WHERE it.owner.id = ?1")
    List<Item> findItemsByOwnerId(Long id);

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

    @Query("SELECT i FROM Item i WHERE i.requestId IN (SELECT ir.id FROM ItemRequest ir WHERE ir.requestor.id = ?1)")
    List<Item> findItemsByItemRequestRequestorId(Long requestorId);
}
