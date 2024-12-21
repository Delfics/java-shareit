package ru.practicum.server.itemrequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.server.itemrequest.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestStorageJpa extends JpaRepository<ItemRequest, Long> {

    @Query("SELECT itreq " +
            "FROM ItemRequest as itreq " +
            "WHERE itreq.requestor.id = ?1 " +
            "ORDER BY itreq.created DESC")
    List<ItemRequest> findAllItemRequestsByUserId(Long userId);

    @Query("SELECT itreq, item " +
            "FROM ItemRequest as itreq " +
            "JOIN Item as item on item.requestId = itreq.id " +
            "WHERE itreq.id = ?1 " +
            "ORDER BY itreq.created DESC")
    Object[] findItemRequestByIdWithItemsForEach(Long id);
}
