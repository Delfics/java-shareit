package ru.practicum.server.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.api.dto.State;
import ru.practicum.api.dto.Status;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.exception.BadRequestException;
import ru.practicum.server.exception.ForbiddenException;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.service.ItemServiceImplJpa;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.service.UserServiceImplJpa;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class BookingServiceImplTest {
    @Autowired
    BookingServiceImpl bookingServiceImpl;

    @Autowired
    ItemServiceImplJpa itemServiceImplJpa;

    @Autowired
    UserServiceImplJpa userServiceImplJpa;

    User booker;
    User owner;
    Item bookedItem;
    Booking booking;

    @BeforeEach
    void setUp() {
        booker = new User();
        booker.setName("Test bookerUser");
        booker.setEmail("bookerUser@gmail.com");
        userServiceImplJpa.create(booker);

        owner = new User();
        owner.setName("Test ownerUser");
        owner.setEmail("ownerUser@gmail.com");
        userServiceImplJpa.create(owner);

        bookedItem = new Item();
        bookedItem.setName("Test bookedItem");
        bookedItem.setDescription("Test bookedItemDescription");
        bookedItem.setOwner(owner);
        bookedItem.setAvailable(true);
        itemServiceImplJpa.createItem(bookedItem, owner.getId());

        booking = new Booking();
        booking.setItem(bookedItem);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
    }

    @Test
    public void testCreateBooking() {
        Booking created = bookingServiceImpl.create(booking, booker.getId());

        assertEquals(created.getStart(), booking.getStart(), "StartLocalDateTime соответсвует");
        assertEquals(created.getEnd(), booking.getEnd(), "EndLocalDateTime соответсвует");
        assertEquals(created.getStatus(), booking.getStatus(), "Status соответсвует");
        assertEquals(created.getItem().getName(), bookedItem.getName(),
                "NameCreatedBookingItem соответсвует BookedItem name");
        assertEquals(created.getItem().getDescription(), bookedItem.getDescription(),
                "DescriptionCreatedBookingItem соответсвует BookedItem description");
        assertEquals(created.getItem().getAvailable(), bookedItem.getAvailable(),
                "AvailableCreatedBookingItem соответсвует BookedItem available");
        assertEquals(created.getBooker().getName(), booker.getName(),
                "NameCreatedBookingBooker соответсвует Booker name");
        assertEquals(created.getBooker().getEmail(), booker.getEmail(),
                "EmailCreatedBookingBooker соответсвует Booker email");
    }

    @Test
    public void testCreateBookingThrowNotFoundBookingItem() {
        Item notFoundItem = null;
        booking.setItem(notFoundItem);

        assertThrows(NotFoundException.class, () -> bookingServiceImpl.create(booking, booker.getId()),
                "BookingItem не найден");
    }

    @Test
    public void testCreateBookingThrowBadRequestExceptionBookingItemGetAvailable() {
        User newOwner = new User();
        newOwner.setName("Test ownerUser1");
        newOwner.setEmail("ownerUser1@gmail.com");
        userServiceImplJpa.create(newOwner);

        Item newBookedItem = new Item();
        newBookedItem.setName("Test bookedItem1");
        newBookedItem.setDescription("Test bookedItemDescription1");
        newBookedItem.setOwner(newOwner);
        newBookedItem.setAvailable(false);
        itemServiceImplJpa.createItem(newBookedItem, newOwner.getId());

        booking.setItem(newBookedItem);

        assertThrows(BadRequestException.class, () -> bookingServiceImpl.create(booking, booker.getId()));
    }

    @Test
    public void testGetAllBookings() {
        User booker1 = new User();
        booker1.setName("Test bookerUser1");
        booker1.setEmail("bookerUser11@gmail.com");
        userServiceImplJpa.create(booker1);

        User owner1 = new User();
        owner1.setName("Test ownerUser1");
        owner1.setEmail("ownerUser1@gmail.com");
        userServiceImplJpa.create(owner1);

        Item bookedItem1 = new Item();
        bookedItem1.setName("Test bookedItem1");
        bookedItem1.setDescription("Test book1edItemDescription");
        bookedItem1.setOwner(owner1);
        bookedItem1.setAvailable(true);
        itemServiceImplJpa.createItem(bookedItem1, owner1.getId());

        Booking booking1 = new Booking();
        booking1.setItem(bookedItem1);
        booking1.setBooker(booker1);
        booking1.setStatus(Status.WAITING);
        booking1.setStart(LocalDateTime.now());
        booking1.setEnd(LocalDateTime.now().plusDays(1));

        Booking booking2 = bookingServiceImpl.create(booking, booker.getId());
        Booking booking3 = bookingServiceImpl.create(booking1, booker1.getId());


        List<Booking> all = bookingServiceImpl.getAll();

        assertTrue(all.size() >= 2, "Bookings соответсвуют количеству");
        assertEquals(booking2.getStart(), all.get(0).getStart(),
                "StartLocalDateTime соответсвует первому Booking");
        assertEquals(booking3.getStart(), all.get(1).getStart(),
                "StartLocalDatetime соответсвует второму Booking");
    }

    @Test
    public void testGetByIdBooking() {
        Booking booking1 = bookingServiceImpl.create(booking, booker.getId());
        Booking byId = bookingServiceImpl.getById(booking1.getId());

        assertEquals(booking1, byId, "Booking идентичны");
    }

    @Test
    public void testGetByIdBookingThrowNotFoundException() {
        booking.setId(999L);

        assertThrows(NotFoundException.class, () -> bookingServiceImpl.getById(booking.getId()),
                "Ловим NotFound, booking не был создан");
    }

    @Test
    public void testFindBookingByBookingIdAndUserId() {
        Booking booking1 = bookingServiceImpl.create(booking, booker.getId());
        Booking bookingByBookingIdAndUserId = bookingServiceImpl.findBookingByBookingIdAndUserId(booking1.getId(), booking1.getBooker().getId());

        assertEquals(booking1, bookingByBookingIdAndUserId, "Booking Идентичны");
    }

    @Test
    public void testFindBookingByBookingIdAndUserIdThrowNotFoundException() {
        Long anotherIdBooking = 999L;
        Long anotherIdBooker = 994L;
        assertThrows(NotFoundException.class,
                () -> bookingServiceImpl.findBookingByBookingIdAndUserId(anotherIdBooking, anotherIdBooker),
                "Ловим NotFound");
    }

    @Test
    public void testFindAllBookingsByBookerIdStateAll() {
        int size = 2;
        bookingServiceImpl.create(booking, booker.getId());

        User owner1 = new User();
        owner1.setName("Test ow1nerUser");
        owner1.setEmail("ow1nerUser@gmail.com");
        userServiceImplJpa.create(owner1);

        Item bookedItem1 = new Item();
        bookedItem1.setName("Test booke1dItem1");
        bookedItem1.setDescription("Test book1edItemDescription");
        bookedItem1.setOwner(owner1);
        bookedItem1.setAvailable(true);
        itemServiceImplJpa.createItem(bookedItem1, owner1.getId());

        Booking booking1 = new Booking();
        booking1.setItem(bookedItem1);
        booking1.setBooker(booker);
        booking1.setStatus(Status.REJECTED);
        booking1.setStart(LocalDateTime.now());
        booking1.setEnd(LocalDateTime.now().plusDays(1));

        bookingServiceImpl.create(booking1, booker.getId());
        List<Booking> allBookingsByBookerId = bookingServiceImpl.findAllBookingsByBookerId(State.ALL, booker.getId());

        assertEquals(size, allBookingsByBookerId.size(), "Bookings данного booker равно 2");

    }

    @Test
    public void testFindAllBookingsForAllItemsByOwnerIdStateAll() {
        int sizeTwo = 2;
        bookingServiceImpl.create(booking, booker.getId());

        Item bookedItem1 = new Item();
        bookedItem1.setName("Test bookedItem1");
        bookedItem1.setDescription("Test bookedItemDescription1");
        bookedItem1.setOwner(owner);
        bookedItem1.setAvailable(true);
        itemServiceImplJpa.createItem(bookedItem1, owner.getId());

        Booking booking1 = new Booking();
        booking1.setItem(bookedItem1);
        booking1.setBooker(booker);
        booking1.setStatus(Status.WAITING);
        booking1.setStart(LocalDateTime.now());
        booking1.setEnd(LocalDateTime.now().plusDays(1));

        bookingServiceImpl.create(booking1, booker.getId());

        List<Booking> allBookingsForAllItemsByOwnerId = bookingServiceImpl.findAllBookingsForAllItemsByOwnerId(State.ALL, owner.getId());

        assertEquals(allBookingsForAllItemsByOwnerId.size(), sizeTwo, "Bookings данного owner равны двум");
    }

    @Test
    public void findAllBookingsForAllItemsByOwnerIdThrowNotFoundException() {
        Long fakeOwnerId = 999L;
        assertThrows(NotFoundException.class, () -> bookingServiceImpl.findAllBookingsForAllItemsByOwnerId(State.ALL, fakeOwnerId));
    }

    @Test
    public void testFilterBookingsStateCurrent() {
        int sizeOne = 1;
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        bookingServiceImpl.create(booking, booker.getId());
        List<Booking> allBookingsByBookerId = bookingServiceImpl.findAllBookingsByBookerId(State.CURRENT, booker.getId());

        assertEquals(allBookingsByBookerId.size(), sizeOne, "Bookings current соответсвует статусу");
    }

    @Test
    public void testFilterBookingsStatePast() {
        int sizeOne = 1;
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().minusSeconds(1));
        bookingServiceImpl.create(booking, booker.getId());
        List<Booking> allBookingsByBookerId = bookingServiceImpl.findAllBookingsByBookerId(State.PAST, booker.getId());

        assertEquals(allBookingsByBookerId.size(), sizeOne, "Bookings past соответсвует статусу");
    }

    @Test
    public void testFilterBookingsStateWaiting() {
        int sizeOne = 1;
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        bookingServiceImpl.create(booking, booker.getId());
        List<Booking> allBookingsByBookerId = bookingServiceImpl.findAllBookingsByBookerId(State.WAITING, booker.getId());

        assertEquals(allBookingsByBookerId.size(), sizeOne, "Bookings waiting соответсвует статусу");
    }

    @Test
    public void testFilterBookingsStateFuture() {
        int sizeOne = 1;
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        bookingServiceImpl.create(booking, booker.getId());
        List<Booking> allBookingsByBookerId = bookingServiceImpl.findAllBookingsByBookerId(State.FUTURE, booker.getId());

        assertEquals(allBookingsByBookerId.size(), sizeOne, "Bookings future соответсвует статусу");
    }

    @Test
    public void testFilterBookingsStateRejected() {
        int sizeOne = 1;
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        bookingServiceImpl.create(booking, booker.getId());
        booking.setStatus(Status.REJECTED);
        List<Booking> allBookingsByBookerId = bookingServiceImpl.findAllBookingsByBookerId(State.REJECTED, booker.getId());

        assertEquals(allBookingsByBookerId.size(), sizeOne, "Bookings rejected соответсвует статусу");
    }

    @Test
    public void testPatchBookingApproved() {
        Boolean approved = true;
        Booking booking1 = bookingServiceImpl.create(booking, booker.getId());
        Booking patch = bookingServiceImpl.patch(booking1.getId(), approved, owner.getId());

        assertEquals(patch.getStatus(), Status.APPROVED, "State Approved изменен");
    }

    @Test
    public void testPatchBookingStateRejectedThrowForbiddenException() {
        Boolean rejected = false;
        Booking booking1 = bookingServiceImpl.create(booking, booker.getId());

        assertThrows(ForbiddenException.class, () -> bookingServiceImpl.patch(booking1.getId(), rejected, owner.getId()));
        Booking byId = bookingServiceImpl.getById(booking1.getId());
        assertEquals(byId.getStatus(), Status.REJECTED, "State Rejected изменен");
    }

    @Test
    public void testFindBookingsBookerIdAndItemId() {
        Booking booking1 = bookingServiceImpl.create(booking, booker.getId());

        Booking bookingByBookerIdAndItemId = bookingServiceImpl.findBookingByBookerIdAndItemId(booker.getId(), bookedItem.getId());

        assertEquals(booking1, bookingByBookerIdAndItemId, "Booking соответсвуют");
    }

    @Test
    public void testExistBookingsByItemList() {
        bookingServiceImpl.create(booking, booker.getId());
        List<Item> items = new ArrayList<>();
        items.add(bookedItem);

        Boolean b = bookingServiceImpl.existBookingsByItemList(items);

        assertTrue(b, "Bookings существуют с такими items");
    }
}
