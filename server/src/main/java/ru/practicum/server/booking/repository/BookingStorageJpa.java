package ru.practicum.server.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.item.model.Item;

import java.util.List;

public interface BookingStorageJpa extends JpaRepository<Booking, Long> {


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
    List<Booking> findAllBookingsByBookerId(Long bookerId);

    @Query("SELECT book " +
            "FROM Booking book " +
            "WHERE book.item.owner.id = ?1")
    List<Booking> findAllBookingsByOwnerId(Long ownerId);

    @Query("SELECT COUNT(book) > 0 " +
            "FROM Booking as book " +
            "WHERE book.item IN ?1")
    Boolean existBookingsByItemList(List<Item> items);
}
