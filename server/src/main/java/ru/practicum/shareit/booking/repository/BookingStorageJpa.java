package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.myenums.Status;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingStorageJpa extends JpaRepository<Booking, Long> {

    @Query("SELECT book " +
            "FROM Booking as book " +
            "WHERE book.item IN ?1")
    List<Booking> findAllBookingByItemList(List<Item> items);

    @Query("SELECT book " +
            "FROM Booking as book " +
            "WHERE book.booker.id = ?1")
    Booking findBookingByBookerId(Long bookerId);

    @Query("SELECT book " +
            "FROM Booking as book " +
            "WHERE book.booker.id = ?1 " +
            "AND book.item.id = ?2")
    Booking findBookingByBookerIdAndItemId(Long bookerId, Long itemId);

    @Query("SELECT book " +
            "FROM Booking as book " +
            "WHERE book.booker.id = ?2 " +
            "OR book.item.owner.id = ?2" +
            "AND book.id = ?1 ")
    Booking findBookingByBookingIdAndUserId(Long bookingId, Long userId);

    @Query("SELECT book " +
            "FROM Booking as book " +
            "WHERE book.booker.id = ?1 " +
            "ORDER BY book.start DESC ")
    List<Booking> findAllBookingByBookerId(Long bookerId);

    @Query("SELECT book " +
            "FROM Booking as book " +
            "WHERE LOWER(book.status) ILIKE (?1) " +
            "AND book.booker.id = ?2 " +
            "ORDER BY book.start DESC ")
    List<Booking> findAllBookingsByStatusAndByBookerId(Status status, Long bookerId);

    @Query("SELECT book " +
            "FROM Booking as book " +
            "WHERE book.id = ?2 " +
            "AND (book.start = ?1 OR book.start < ?1 ) " +
            "AND book.end > ?1" +
            "ORDER BY book.start DESC ")
    List<Booking> findAllBookingsByCurrentTimeAndBookerId(LocalDateTime now, Long bookerId);


    @Query("SELECT book " +
            "FROM Booking as book " +
            "WHERE book.id = ?2 " +
            "AND book.end < ?1 " +
            "ORDER BY book.start DESC")
    List<Booking> findAllBookingsByPastTimeAndBookerId(LocalDateTime now, Long bookerId);

    @Query("SELECT book " +
            "FROM Booking as book " +
            "WHERE book.id = ?2 " +
            "AND book.start > ?1 " +
            "ORDER BY book.start DESC")
    List<Booking> findAllBookingsByFutureTimeAndBookerId(LocalDateTime now, Long bookerId);

    @Query("SELECT book " +
            "FROM Booking as book " +
            "WHERE book.item.owner.id = ?1 " +
            "ORDER BY book.start DESC ")
    List<Booking> findAllBookingsWithAllItemsForOwnerId(Long currentUser);

    @Query("SELECT book " +
            "FROM Booking as book " +
            "WHERE book.item.owner.id = ?2 " +
            "AND (book.start = ?1 OR book.start < ?1) " +
            "AND book.end > ?1 " +
            "ORDER BY book.start DESC")
    List<Booking> findAllBookingsWithAllItemsByCurrentTimeAndOwnerId(LocalDateTime now, Long ownerId);

    @Query("SELECT book " +
            "FROM Booking as book " +
            "WHERE book.item.owner.id = ?2 " +
            "AND book.end < ?1 " +
            "ORDER BY book.start DESC")
    List<Booking> findAllBookingsWithAllItemsByPastTimeAndOwnerId(LocalDateTime now, Long ownerId);

    @Query("SELECT book " +
            "FROM Booking as book " +
            "WHERE book.item.owner.id = ?2 " +
            "AND book.start > ?1 " +
            "ORDER BY book.start DESC")
    List<Booking> findAllBookingsWithAllItemsByFutureTimeAndOwnerId(LocalDateTime now, Long ownerId);

    @Query("SELECT book " +
            "FROM Booking as book " +
            "WHERE book.item.owner.id = ?2" +
            "AND LOWER(book.status) ILIKE (?1)" +
            "ORDER BY book.start DESC ")
    List<Booking> findAllBookingsWithAllItemsByStatusAndOwnerId(Status status, Long ownerId);

    @Query("SELECT COUNT(book) > 0 " +
            "FROM Booking book " +
            "WHERE book.booker.id = ?1 " +
            "AND book.item.id = ?2")
    Boolean existsBookingByUserIdAndItemId(Long userId, Long itemId);

    @Query("SELECT COUNT(book) > 0 " +
            "FROM Booking as book " +
            "WHERE book.item IN ?1")
    Boolean existBookingsByItemList(List<Item> items);
}
