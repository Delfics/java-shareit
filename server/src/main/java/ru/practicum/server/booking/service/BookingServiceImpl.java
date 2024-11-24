package ru.practicum.server.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.practicum.api.State;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.api.Status;
import ru.practicum.server.booking.repository.BookingStorageJpa;
import ru.practicum.api.BookingDto;
import ru.practicum.server.exception.BadRequestException;
import ru.practicum.server.exception.ForbiddenException;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.item.mappers.ItemMapper;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.service.ItemService;
import ru.practicum.server.user.mappers.UserMapper;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl {
    private final BookingStorageJpa bookingStorage;
    private final UserService userService;
    private final ItemService itemService;

    public List<Booking> getAll() {
        return bookingStorage.findAll();
    }

    public Booking create(Booking booking, Long userId) {
        if (booking.getItem() == null) {
            throw new NotFoundException("Booking item не найден");
        }
        Item foundItem = itemService.getById(booking.getItem().getId());
        booking.setItem(foundItem);
        User foundUser = userService.getById(userId);
        booking.setBooker(foundUser);
        if (!booking.getItem().getAvailable()) {
            throw new BadRequestException("Booking - item занят в данный момент");
        }
        booking.setStatus(Status.WAITING);
        bookingStorage.save(booking);
        return bookingStorage.findById(booking.getId()).get();
    }

    public Booking getById(Long id) {
        log.debug("Поиск booking по {}", id);
        Optional<Booking> byId = bookingStorage.findById(id);
        if (byId.isPresent()) {
            return byId.get();
        } else {
            throw new NotFoundException("Booking с id " + id + " не найден ");
        }
    }

    public Booking findBookingByBookingIdAndUserId(Long bookingId, Long userId) {
        log.debug("Поиск брони по bookingId - {} и userId - {}", bookingId, userId);
        Booking booking = bookingStorage.findBookingByBookingIdAndUserId(bookingId, userId);
        if (booking == null) {
            throw new NotFoundException("Такого booking не существует для такого пользователя - " + bookingId);
        }
        return booking;
    }


    public List<Booking> findAllBookingsByBookerId(State state, Long bookerId) {
        Booking bookingByBookerId = bookingStorage.findBookingByBookerId(bookerId);
        LocalDateTime timeNow = LocalDateTime.now();
        log.debug("Поиск брони по bookerId - {}", bookerId);

        if (state.equals(State.CURRENT) && (bookingByBookerId.getStart().equals(timeNow) || bookingByBookerId.getStart().isBefore(timeNow)) &&
                bookingByBookerId.getEnd().isAfter(timeNow)) {
            return bookingStorage.findAllBookingsByCurrentTimeAndBookerId(timeNow, bookerId);
        } else if (state.equals(State.PAST) && bookingByBookerId.getEnd().isBefore(timeNow)) {
            return bookingStorage.findAllBookingsByPastTimeAndBookerId(timeNow, bookerId);
        } else if (state.equals(State.FUTURE) && bookingByBookerId.getStart().isAfter(timeNow)) {
            return bookingStorage.findAllBookingsByFutureTimeAndBookerId(timeNow, bookerId);
        } else if (state.equals(State.WAITING)) {
            return bookingStorage.findAllBookingsByStatusAndByBookerId(Status.WAITING, bookerId);
        } else if (state.equals(State.REJECTED)) {
            return bookingStorage.findAllBookingsByStatusAndByBookerId(Status.REJECTED, bookerId);
        } else {
            return bookingStorage.findAllBookingByBookerId(bookerId);
        }
    }

    public List<Booking> findAllBookingsWithAllItemsByOwnerId(State state, Long ownerId) {
        int zero = 0;
        LocalDateTime timeNow = LocalDateTime.now();
        Booking bookingByBookerId = bookingStorage.findBookingByBookerId(ownerId);
        log.debug("Поиск брони со всеми items по ownerId - {}", ownerId);

        if (itemService.findItemsByOwnerId(ownerId).size() > zero) {
            if (state.equals(State.CURRENT) && (bookingByBookerId.getStart().equals(timeNow) || bookingByBookerId.getStart().isBefore(timeNow)) &&
                    bookingByBookerId.getEnd().isAfter(timeNow)) {
                return bookingStorage.findAllBookingsWithAllItemsByCurrentTimeAndOwnerId(timeNow, ownerId);
            } else if (state.equals(State.PAST) && bookingByBookerId.getEnd().isBefore(timeNow)) {
                return bookingStorage.findAllBookingsWithAllItemsByPastTimeAndOwnerId(timeNow, ownerId);
            } else if (state.equals(State.FUTURE) && bookingByBookerId.getStart().isAfter(timeNow)) {
                return bookingStorage.findAllBookingsWithAllItemsByFutureTimeAndOwnerId(timeNow, ownerId);
            } else if (state.equals(State.WAITING)) {
                return bookingStorage.findAllBookingsWithAllItemsByStatusAndOwnerId(Status.WAITING, ownerId);
            } else if (state.equals(State.REJECTED)) {
                return bookingStorage.findAllBookingsWithAllItemsByStatusAndOwnerId(Status.REJECTED, ownerId);
            } else {
                return bookingStorage.findAllBookingsWithAllItemsForOwnerId(ownerId);
            }
        } else {
            throw new NotFoundException("У пользователя " + ownerId + " нет вещей");
        }
    }

    public Booking patch(Long bookingId, Boolean approved, Long ownerId) {
        Optional<Booking> booking = bookingStorage.findById(bookingId);
        Long bookingItemOwnerId = booking.get().getItem().getOwner().getId();
        if (booking.get().getStatus() == Status.WAITING && approved) {
            if (bookingItemOwnerId.equals(ownerId)) {
                booking.get().setStatus(Status.APPROVED);
                bookingStorage.save(booking.get());
                log.debug("Успшено изменено описание booking {}", booking.get().getId());
            } else {
                booking.get().setStatus(Status.REJECTED);
                bookingStorage.save(booking.get());
                throw new ForbiddenException("Отказано В доступе");
            }
        }
        return bookingStorage.findById(bookingId).get();
    }

    public BookingDto addItemIntoBookingDto(BookingDto bookingDto) {
        log.debug("Добавил item в bookingDto");
        Item byId = itemService.getById(bookingDto.getItemId());
        bookingDto.setItem(ItemMapper.toItemDto(byId));
        return bookingDto;
    }

    public BookingDto addBookerToBookingDto(BookingDto bookingDto, Long userId) {
        log.debug("Добавил booker в bookingDto");
        User byId = userService.getById(userId);
        bookingDto.setBooker(UserMapper.toUserDto(byId));
        return bookingDto;
    }

    public Boolean existsBookingByUserIdAndItemId(Long userId, Long itemId) {
        return bookingStorage.existsBookingByUserIdAndItemId(userId, itemId);
    }

    public List<Booking> findAllBookingByItemList(List<Item> items) {
        return bookingStorage.findAllBookingByItemList(items);
    }

    public Booking findBookingByBookerIdAndItemId(Long bookerId, Long itemId) {
        return bookingStorage.findBookingByBookerIdAndItemId(bookerId, itemId);
    }

    public Boolean existBookingsByItemList(List<Item> items) {
        return bookingStorage.existBookingsByItemList(items);
    }
}
