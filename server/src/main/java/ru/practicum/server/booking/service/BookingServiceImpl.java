package ru.practicum.server.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.api.dto.State;
import ru.practicum.api.dto.Status;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.repository.BookingStorageJpa;
import ru.practicum.server.exception.BadRequestException;
import ru.practicum.server.exception.ForbiddenException;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.service.ItemService;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        if (booking.getStart() == null || booking.getEnd() == null) {
            throw new BadRequestException("Booking start и end должны быть указаны");
        }
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
        List<Booking> allBookingsByBookerId = bookingStorage.findAllBookingsByBookerId(bookerId);
        log.debug("Поиск брони по bookerId - {}", bookerId);
        return filterBookings(state, allBookingsByBookerId);
    }

    public List<Booking> findAllBookingsForAllItemsByOwnerId(State state, Long ownerId) {
        int zero = 0;
        List<Booking> allBookingsByBookerId = bookingStorage.findAllBookingsByOwnerId(ownerId);
        log.debug("Поиск брони по ownerId - {}", ownerId);

        if (itemService.findItemsByOwnerId(ownerId).size() > zero) {
            return filterBookings(state, allBookingsByBookerId);
        } else {
            throw new NotFoundException("У пользователя " + ownerId + " нет вещей");
        }
    }

    public Booking patch(Long bookingId, Boolean approved, Long ownerId) {
        Optional<Booking> booking = bookingStorage.findById(bookingId);
        Long bookingItemOwnerId = booking.get().getItem().getOwner().getId();
        if (booking.get().getStatus() == Status.WAITING && approved && bookingItemOwnerId.equals(ownerId)) {
            booking.get().setStatus(Status.APPROVED);
            bookingStorage.save(booking.get());
            log.debug("Успшено изменено описание booking {}", booking.get().getId());
        } else {
            booking.get().setStatus(Status.REJECTED);
            bookingStorage.save(booking.get());
            throw new ForbiddenException("Отказано В доступе");
        }
        return bookingStorage.findById(bookingId).get();
    }

    private List<Booking> filterBookings(State state, List<Booking> allBookings) {
        LocalDateTime timeNow = LocalDateTime.now();
        List<Booking> filteredBookings = allBookings.stream()
                .filter(booking -> {
                    switch (state) {
                        case CURRENT:
                            return booking.getStart().isBefore(timeNow) && booking.getEnd().isAfter(timeNow);
                        case PAST:
                            return booking.getEnd().isBefore(timeNow);
                        case FUTURE:
                            return booking.getStart().isAfter(timeNow);
                        case WAITING:
                            return booking.getStatus() == Status.WAITING;
                        case REJECTED:
                            return booking.getStatus() == Status.REJECTED;
                        case ALL:
                        default:
                            return true;
                    }
                })
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .collect(Collectors.toList());
        return filteredBookings;
    }

    public Booking findBookingByBookerIdAndItemId(Long bookerId, Long itemId) {
        return bookingStorage.findBookingByBookerIdAndItemId(bookerId, itemId);
    }

    public Boolean existBookingsByItemList(List<Item> items) {
        return bookingStorage.existBookingsByItemList(items);
    }
}
