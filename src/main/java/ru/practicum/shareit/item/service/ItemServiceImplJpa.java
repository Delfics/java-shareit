package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.myenums.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemStorageJpa;
import ru.practicum.shareit.item.model.ItemWithBookingsAndComments;
import ru.practicum.shareit.item.validator.Valid;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.repository.CommentStorageJpa;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.mappers.UserMapper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Primary
@Slf4j
public class ItemServiceImplJpa implements ItemService {
    private final ItemStorageJpa itemStorageJpa;
    private final UserService userService;
    private final CommentStorageJpa commentStorageJpa;
    private final BookingServiceImpl bookingService;

    @Autowired
    public ItemServiceImplJpa(ItemStorageJpa itemStorageJpa, UserService userService,
                              CommentStorageJpa commentStorageJpa, @Lazy BookingServiceImpl bookingService) {
        this.itemStorageJpa = itemStorageJpa;
        this.userService = userService;
        this.commentStorageJpa = commentStorageJpa;
        this.bookingService = bookingService;
    }

    public Collection<Item> getAll() {
        return itemStorageJpa.findAll();
    }

    public Item getById(Long id) {
        Optional<Item> byId = itemStorageJpa.findById(id);
        if (byId.isPresent()) {
            log.debug("Найден успешно item {}", byId.get().getId());
            return byId.get();
        } else {
            throw new NotFoundException("Item " + id + " не найден");
        }
    }

    public Item createItem(Item item, Long userId) {
        Valid.throwExIfUserIdNull(userId, item);
        item.setOwner(userService.getById(userId));
        Valid.throwExIfNameOrDescriptionAreEmptyAndAvailableIsNull(item);
        item.setAvailable(item.getAvailable());
        Item itemCreated = itemStorageJpa.save(item);
        log.debug("Создан успешно item {}", itemCreated.getName());
        return itemCreated;
    }

    public Comment createComment(Comment comment, Long userId, Long itemId) {
        LocalDateTime now = LocalDateTime.now();
        Booking bookingByBookerId = bookingService.findBookingByBookerIdAndItemId(userId, itemId);
        if (bookingByBookerId.getStatus().equals(Status.APPROVED) && bookingByBookerId.getEnd().isBefore(now)) {
            commentStorageJpa.save(comment);
            Comment commentById = commentStorageJpa.findById(comment.getId()).get();
            log.debug("Комментарий успешно создан {}", commentById.getId());
            return commentById;
        } else {
            throw new BadRequestException("Такой пользователь не может оставить комментарий");
        }
    }

    public Item update(Item newItem) {
        Item update = itemStorageJpa.save(newItem);
        log.debug("Обновлён успешно item {}", update.getName());
        return update;
    }

    public void deleteById(Long id) {
        if (itemStorageJpa.findById(id).isPresent()) {
            itemStorageJpa.deleteById(id);
            log.debug("Удалён успешно item {}", id);
        } else {
            throw new NotFoundException("Предмет с таким id не найден " + id);
        }
    }

    public Item patch(Long itemId, Item item, Long userId) {
        if (!Objects.equals(getById(itemId).getOwner().getId(), userId)) {
            throw new NotFoundException(userId + " не владелец вещи " + itemId);
        }
        Item byId = getById(itemId);
        if (item.getName() != null) {
            byId.setName(item.getName());
        }
        if (item.getDescription() != null) {
            byId.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            byId.setAvailable(item.getAvailable());
        }
        Item update = update(byId);
        log.debug("Успшено изменено описание item {}", update.getId());
        return update;
    }

    public Collection<Item> findItemsByText(String text) {
        log.debug("Поиск предметов по названию - {}", text);
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemStorageJpa.findItemsByText(text);
    }


    public ItemWithBookingsAndComments findItemWithComments(Long itemId) {
        log.debug("Поиск предметов по id с комментариями - {}", itemId);
        Item byId = getById(itemId);
        List<Comment> commentByItemId = commentStorageJpa.findCommentByItemId(byId.getId());
        List<String> listComments = commentByItemId.stream()
                .map(Comment::getText)
                .toList();
        return new ItemWithBookingsAndComments(byId, null, null, listComments);
    }

    public List<Item> findItemsByOwnerId(Long id) {
        return itemStorageJpa.findItemsByOwnerId(id);
    }


    public List<ItemWithBookingsAndComments> findItemsWithCommentsBookingByUserId(Long userId) {
        log.debug("Поиск предметов по id владельца c комментариями - {}", userId);
        List<Item> itemsByOwnerId = itemStorageJpa.findItemsByOwnerId(userId);
        List<Comment> commentsByItemsSortedByDate = commentStorageJpa.findCommentsByItemsSortedByDate(itemsByOwnerId);

        if (bookingService.existBookingsByItemList(itemsByOwnerId)) {
            log.debug("Добавлены условия поиска с последним и ближайшим временем брони - {}", userId);
            List<Object[]> bookingData = itemStorageJpa.findWithLastAndCloserBookingByOwnerId(LocalDateTime.now(), userId);

            List<ItemWithBookingsAndComments> itemList = bookingData.stream().map(data -> {
                Item item = (Item) data[0];
                LocalDateTime nextBookingTime = (LocalDateTime) data[1];
                LocalDateTime lastBookingTime = (LocalDateTime) data[2];
                return new ItemWithBookingsAndComments(item, nextBookingTime, lastBookingTime);
            }).toList();

            Map<Item, List<String>> commentsMap = commentsByItemsSortedByDate.stream()
                    .collect(Collectors.groupingBy(Comment::getItem,
                            Collectors.mapping(Comment::getText, Collectors.toList())));

            itemList.forEach(itemWithBookings -> {
                List<String> commentTexts = commentsMap.getOrDefault(itemWithBookings.getItem(), List.of());

                itemWithBookings.setComments(commentTexts);
            });

        }

        Map<Item, List<String>> commentsMap = commentsByItemsSortedByDate.stream()
                .collect(Collectors.groupingBy(Comment::getItem,
                        Collectors.mapping(Comment::getText, Collectors.toList())));

        List<ItemWithBookingsAndComments> itemList = itemsByOwnerId.stream()
                .map(item -> {
                    ItemWithBookingsAndComments itemWithBookings = new ItemWithBookingsAndComments();
                    itemWithBookings.setItem(item);
                    return itemWithBookings;
                })
                .toList();

        itemList.forEach(itemWithBookingsAndComments -> {
            List<String> commentTexts = commentsMap.getOrDefault(itemWithBookingsAndComments.getItem(), List.of());

            itemWithBookingsAndComments.setComments(commentTexts);
        });

        return itemList;
    }

    public List<Item> findItemsByItemRequestRequestorId(Long requestorId) {
        return itemStorageJpa.findItemsByItemRequestRequestorId(requestorId);
    }

    public CommentDto addItemToCommentDto(CommentDto commentDto, Long itemId) {
        Item byId = getById(itemId);
        commentDto.setItem(ItemMapper.toItemDto(byId));
        log.debug("Добавил item в commentDto");
        return commentDto;
    }

    public CommentDto addAuthorToCommentDto(CommentDto commentDto, Long userId) {
        User byId = userService.getById(userId);
        commentDto.setAuthor(UserMapper.toUserDto(byId));
        log.debug("Добавил author в commentDto");
        return commentDto;
    }
}
