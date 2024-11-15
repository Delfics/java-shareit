package ru.practicum.shareit.itemrequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.itemrequest.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestStorageJpa extends JpaRepository<ItemRequest, Long> {

    @Query("SELECT itreq " +
            "FROM ItemRequest as itreq " +
            "WHERE itreq.requestor.id = ?1 " +
            "ORDER BY itreq.created DESC")
    List<ItemRequest> findAllItemRequestsByUserId(Long userId);

    /*@Query("SELECT new ru.practicum.shareit.itemrequest.model.ItemRequestWithListItems(itreq, " +
            "   (SELECT i FROM Item i WHERE i.itemRequest = itreq)) " +
            "FROM ItemRequest itreq " +
            "WHERE itreq.requestor.id = ?1 " +
            "ORDER BY itreq.created DESC")
    List<ItemRequestWithListItems> findAllItemRequestsWithItemsForEach(Long userId);*/

    /*@Query("SELECT new ru.practicum.shareit.itemrequest.model.ItemRequestWithItems(itreq, " +
            "(SELECT i FROM Item i WHERE i.itemRequest.id = itreq.id)) " +
            "FROM ItemRequest itreq " +
            "WHERE itreq.requestor.id = ?1 " +
            "ORDER BY itreq.created DESC")
    List<ItemRequestWithItems> findAllItemRequestsWithItemsForEach(Long userId);*/

    @Query("SELECT itreq, item " +
            "FROM ItemRequest as itreq " +
            "JOIN Item as item on item.requestId = itreq.id " +
            "WHERE itreq.id = ?1 " +
            "ORDER BY itreq.created DESC")
    Object[] findItemRequestByIdWithItemsForEach(Long id);
}
