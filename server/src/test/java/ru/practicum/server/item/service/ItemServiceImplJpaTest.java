package ru.practicum.server.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.service.BookingServiceImpl;
import ru.practicum.server.exception.BadRequestException;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.exception.ValidationException;
import ru.practicum.server.item.model.Comment;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.model.ItemWithBookingsAndComments;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.service.UserServiceImplJpa;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ItemServiceImplJpaTest {

    @Autowired
    private ItemServiceImplJpa itemServiceImplJpa;

    @Autowired
    private UserServiceImplJpa userServiceImplJpa;

    @Autowired
    private BookingServiceImpl bookingServiceImpl;

    private Item item;
    private User user;
    private User booker;
    private Comment comment;
    private Booking booking;

    @BeforeEach
    public void setUp() {
        item = new Item();
        item.setName("Test item");
        item.setDescription("Test item description");
        item.setAvailable(true);

        user = new User();
        user.setName("Test User");
        user.setEmail("testuser@example.com");
        userServiceImplJpa.create(user);

        booker = new User();
        booker.setName("Test Booker");
        booker.setEmail("testbooker@example.com");
        userServiceImplJpa.create(booker);

        comment = new Comment();
        comment.setText("Test comment text");
        comment.setUser(booker);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
    }

    @Test
    public void testCreateItem() {
        Item savedItem = itemServiceImplJpa.createItem(item, user.getId());

        assertNotNull(savedItem);
        assertEquals("Test item", savedItem.getName(), "Name соответствует");
        assertEquals("Test item description", savedItem.getDescription(), "Description тоже");
        assertNotNull(savedItem.getOwner(), "Owner существует");
    }

    @Test
    public void testCreateItemValidationExceptionIfUserIdIsNull() {
        user.setId(null);

        assertThrows(ValidationException.class, () -> itemServiceImplJpa.createItem(item, user.getId()));
    }

    @Test
    public void testCreateItemValidationExceptionIfNameOrDescriptionAreEmptyAndAvailableIsNull(){
        item.setName("");

        assertThrows(ValidationException.class, () -> itemServiceImplJpa.createItem(item, user.getId()),
                "Name пустое");

        item.setName("Test name");
        item.setDescription("");

        assertThrows(ValidationException.class, () -> itemServiceImplJpa.createItem(item, user.getId()),
                "Description пустое");

        item.setDescription("Test description");
        item.setAvailable(null);

        assertThrows(ValidationException.class, () -> itemServiceImplJpa.createItem(item, user.getId()),
                "Available null");
    }

    @Test
    public void testUpdateItem() {
        String newName = "Update item";
        String newDescription = "Update item description";

        item.setName(newName);
        item.setDescription(newDescription);

        Item update = itemServiceImplJpa.update(item);

        assertEquals(newName, update.getName(), "Name обновилось и соответствует");
        assertEquals(newDescription, update.getDescription(), "Description обновилось и соответсвует");
    }

    @Test
    public void testPatchItem() {
        itemServiceImplJpa.createItem(item, user.getId());

        String newName = "Update item";
        String newDescription = "Update item description";

        item.setName(newName);
        Item patch = itemServiceImplJpa.patch(item.getId(), item, user.getId());
        assertEquals(newName, patch.getName(), "Имя обновлено");

        item.setDescription(newDescription);
        patch = itemServiceImplJpa.patch(item.getId(), item, user.getId());
        assertEquals(newDescription, patch.getDescription(), "Описание обновлено");
    }

    @Test
    public void testPatchItemFakeOwnerThrowNotFoundException() {
        itemServiceImplJpa.createItem(item, user.getId());
        String newName = "Update item";
        String newDescription = "Update item description";
        item.setName(newName);
        item.setDescription(newDescription);

        User fakeUser = new User();
        fakeUser.setName("Test User1");
        fakeUser.setEmail("testuser22@example.com");

        userServiceImplJpa.create(fakeUser);

        assertThrows(NotFoundException.class, () ->itemServiceImplJpa.patch(item.getId(), item, fakeUser.getId()));
    }

    @Test
    public void testDeleteItemThrowNotFoundException() {
        itemServiceImplJpa.createItem(item, user.getId());
        itemServiceImplJpa.deleteById(item.getId());

        assertThrows(NotFoundException.class, () -> itemServiceImplJpa.getById(item.getId()), "Item удален и не существует более");
    }

    @Test
    public void testFindItemsByText() {
        String needToFindName = "Necessary item";
        String needToFindDescription = "Necessary item description";
        item.setName(needToFindName);
        itemServiceImplJpa.createItem(item, user.getId());

        Collection<Item> itemsByText = itemServiceImplJpa.findItemsByText(needToFindName);
        ArrayList<Item> items = new ArrayList<>(itemsByText);
        Item item1 = items.get(0);

        assertEquals(needToFindName, item1.getName(), "Найден текст по name");

        item.setDescription(needToFindDescription);
        itemServiceImplJpa.update(item);

        Collection<Item> itemsByText1 = itemServiceImplJpa.findItemsByText(needToFindDescription);
        ArrayList<Item> items2 = new ArrayList<>(itemsByText1);
        Item item2 = items2.get(0);

        assertEquals(needToFindDescription, item2.getDescription(), "Найден текст по description");
    }

    @Test
    public void testFindItemsByOwnerId() {
        Item anotherItem = new Item();
        anotherItem.setName("Another Item");
        anotherItem.setDescription("Another item description");
        anotherItem.setAvailable(true);
        anotherItem.setOwner(user);

        itemServiceImplJpa.createItem(item, user.getId());
        itemServiceImplJpa.createItem(anotherItem, user.getId());

        Collection<Item> itemsByOwnerId = itemServiceImplJpa.findItemsByOwnerId(user.getId());

        assertTrue(itemsByOwnerId.size() >= 2, "Содержит 2 item которые созданые выше имея 1го владельца");

    }

    @Test
    public void testCreateComment() {
        Boolean approved = true;

        item.setOwner(user);
        itemServiceImplJpa.createItem(item, user.getId());

        booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setBooker(booker);
        booking.setItem(item);
        bookingServiceImpl.create(booking, booker.getId());
        bookingServiceImpl.patch(booking.getId(), approved, user.getId());

        itemServiceImplJpa.createComment(comment, booker.getId(), item.getId());

        assertEquals(comment.getItem().getName(), item.getName(), "Комментарий к Item создан");
        assertEquals(comment.getUser().getName(), booker.getName(),
                "Booker то есть заказчик оставил комментарий, Имена соответсвуют");
    }

    @Test
    public void testCreateCommentThrowBadRequestException() {
        itemServiceImplJpa.createItem(item, user.getId());

        booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setBooker(booker);
        booking.setItem(item);
        bookingServiceImpl.create(booking, booker.getId());

        assertThrows(BadRequestException.class, () -> itemServiceImplJpa.createComment(comment, booker.getId(), item.getId()),
                "Словили BadRequestException при неправильных условиях");
    }

    @Test
    public void testFindItemWithComments() {
        Boolean approved = true;
        int size = 2;

        item.setOwner(user);
        itemServiceImplJpa.createItem(item, user.getId());

        booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setBooker(booker);
        booking.setItem(item);
        bookingServiceImpl.create(booking, booker.getId());
        bookingServiceImpl.patch(booking.getId(), approved, user.getId());

        itemServiceImplJpa.createComment(comment, booker.getId(), item.getId());

        User anotherBooker = new User();
        anotherBooker.setName("Another Booker");
        anotherBooker.setEmail("anotherbooker@example.com");
        userServiceImplJpa.create(anotherBooker);

        Booking anotherBooking = new Booking();
        anotherBooking.setStart(LocalDateTime.now().minusDays(4));
        anotherBooking.setEnd(LocalDateTime.now().minusDays(2));
        anotherBooking.setBooker(anotherBooker);
        anotherBooking.setItem(item);
        bookingServiceImpl.create(anotherBooking, anotherBooker.getId());
        bookingServiceImpl.patch(anotherBooking.getId(), approved, user.getId());

        Comment anotherComment = new Comment();
        anotherComment.setItem(item);
        anotherComment.setUser(anotherBooker);
        anotherComment.setText("Another comment");
        anotherComment.setCreated(LocalDateTime.now());
        itemServiceImplJpa.createComment(anotherComment, anotherBooker.getId(), item.getId());

        ItemWithBookingsAndComments itemWithComments = itemServiceImplJpa.findItemWithComments(item.getId());
        String textComment1 = itemWithComments.getComments().get(0);
        String textComment2 = itemWithComments.getComments().get(1);

        assertEquals(itemWithComments.getItem().getName(), item.getName(), "Name item соответсвует");
        assertEquals(itemWithComments.getItem().getDescription(), item.getDescription(), "Description соответсвует");
        assertEquals(itemWithComments.getComments().size(), size, "Comment создалось 2 шт и соответсвуют количеству");
        assertEquals(textComment1, comment.getText(), "Text Comment созданных и полученных идентичен");
        assertEquals(textComment2, anotherComment.getText(), "Text AnotherComment созданных и полученных идентичен");
    }

    @Test
    public void testFindItemsWithCommentsBookingByUserId() {
        Boolean approved = true;
        int sizeTwo = 2;

        item.setOwner(user);
        itemServiceImplJpa.createItem(item, user.getId());

        booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setBooker(booker);
        booking.setItem(item);
        bookingServiceImpl.create(booking, booker.getId());
        bookingServiceImpl.patch(booking.getId(), approved, user.getId());

        itemServiceImplJpa.createComment(comment, booker.getId(), item.getId());

        User anotherBooker = new User();
        anotherBooker.setName("Another Booker");
        anotherBooker.setEmail("anotherbooker@example.com");
        userServiceImplJpa.create(anotherBooker);

        Booking anotherBooking = new Booking();
        anotherBooking.setStart(LocalDateTime.now().minusDays(4));
        anotherBooking.setEnd(LocalDateTime.now().minusDays(2));
        anotherBooking.setBooker(anotherBooker);
        anotherBooking.setItem(item);
        bookingServiceImpl.create(anotherBooking, anotherBooker.getId());
        bookingServiceImpl.patch(anotherBooking.getId(), approved, user.getId());

        Comment anotherComment = new Comment();
        anotherComment.setItem(item);
        anotherComment.setUser(anotherBooker);
        anotherComment.setText("Another comment");
        anotherComment.setCreated(LocalDateTime.now());
        itemServiceImplJpa.createComment(anotherComment, anotherBooker.getId(), item.getId());

        Booking nextBooking = new Booking();
        nextBooking.setStart(LocalDateTime.now().plusDays(1));
        nextBooking.setEnd(LocalDateTime.now().plusDays(2));
        nextBooking.setBooker(anotherBooker);
        nextBooking.setItem(item);
        bookingServiceImpl.create(nextBooking, anotherBooker.getId());
        bookingServiceImpl.patch(nextBooking.getId(), approved, user.getId());

        List<ItemWithBookingsAndComments> itemsWithCommentsBookingByUserId = itemServiceImplJpa.findItemsWithCommentsBookingByUserId(user.getId());
        List<String> comments = itemsWithCommentsBookingByUserId.get(0).getComments();
        Item gotItem = itemsWithCommentsBookingByUserId.get(0).getItem();

        assertEquals(comments.size(), sizeTwo, "Количество комментариев соответсвует 2");
        assertEquals(comments.get(0), anotherComment.getText(),
                "Текст комментария соответсвует - Another comment, а так же сортировка от нового к старому");
        assertEquals(comments.get(1), comment.getText(), "Текст комментария соответсвует - Test comment text");
        assertEquals(item.getName(), gotItem.getName(), "Полученный Name Item соответсвует созданному");
    }
}
