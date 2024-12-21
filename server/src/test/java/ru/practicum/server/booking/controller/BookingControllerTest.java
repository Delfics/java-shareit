package ru.practicum.server.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.api.dto.BookingDto;
import ru.practicum.api.dto.State;
import ru.practicum.api.dto.Status;
import ru.practicum.server.booking.mappers.BookingMapper;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.service.BookingServiceImpl;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.user.model.User;
import ru.practicum.server.utils.HttpProperties;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    @InjectMocks
    private BookingController bookingController;
    private BookingServiceImpl bookingServiceImplJpa = Mockito.mock(BookingServiceImpl.class);
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private Booking booking;
    private BookingDto bookingDto;
    private Item item;
    private User booker;
    private User owner;
    private final DateTimeFormatter formatJson = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();

        booker = new User();
        booker.setId(1L);
        booker.setName("Test name booker");
        booker.setEmail("Booker228@gmail.com");

        owner = new User();
        owner.setId(2L);
        owner.setName("Test owner requestor");
        owner.setEmail("Owner228@gmail.com");

        item = new Item();
        item.setId(1L);
        item.setName("Test name item");
        item.setDescription("Test description");
        item.setOwner(owner);
        item.setAvailable(true);

        booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(Status.WAITING);


        bookingDto = BookingMapper.toBookingDto(booking);
        bookingDto.setItemId(item.getId());
    }

    @Test
    public void testCreateBooking() throws Exception {
        String startString = booking.getStart().format(formatJson);
        String endString = booking.getEnd().format(formatJson);

        when(bookingServiceImplJpa.create(any(Booking.class), anyLong())).thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpProperties.xSharerUserId, booker.getId()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(startString)))
                .andExpect(jsonPath("$.end", is(endString)))
                .andExpect(jsonPath("$.status", is(booking.getStatus().name())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.item.description", is(bookingDto.getItem().getDescription())))
                .andExpect(jsonPath("$.item.available", is(bookingDto.getItem().getAvailable())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$.booker.email", is(bookingDto.getBooker().getEmail())));

        verify(bookingServiceImplJpa, times(1)).create(any(Booking.class), anyLong());
    }

    @Test
    public void testPatchBooking() throws Exception {
        booking.setStatus(Status.APPROVED);
        bookingDto = BookingMapper.toBookingDto(booking);
        when(bookingServiceImplJpa.patch(anyLong(), anyBoolean(), anyLong())).thenReturn(booking);

        mockMvc.perform(patch("/bookings/{bookingId}?approved=true", booking.getId())
                        .header(HttpProperties.xSharerUserId, booker.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name())));

        verify(bookingServiceImplJpa, times(1)).patch(anyLong(), anyBoolean(), anyLong());
    }

    @Test
    public void testGetBookingByBookingId() throws Exception {
        when(bookingServiceImplJpa.findBookingByBookingIdAndUserId(anyLong(), anyLong())).thenReturn(booking);

        mockMvc.perform(get("/bookings/{bookingId}", booking.getId())
                        .header(HttpProperties.xSharerUserId, booker.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class));

        verify(bookingServiceImplJpa, times(1)).findBookingByBookingIdAndUserId(anyLong(), anyLong());
    }

    @Test
    public void testGetAllBookingsByBookerId() throws Exception {
        Item anotherItem = new Item();
        anotherItem.setId(2L);
        anotherItem.setName("Test another name");
        anotherItem.setDescription("Test another description");
        anotherItem.setOwner(owner);
        anotherItem.setAvailable(true);

        Booking anotherBooking = new Booking();
        anotherBooking.setId(2L);
        anotherBooking.setStart(LocalDateTime.now().plusMinutes(1));
        anotherBooking.setEnd(LocalDateTime.now().plusDays(2));
        anotherBooking.setBooker(booker);
        anotherBooking.setItem(anotherItem);

        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        bookingList.add(anotherBooking);

        List<BookingDto> bookingDtoList = bookingList.stream()
                .map(BookingMapper::toBookingDto)
                .toList();

        when(bookingServiceImplJpa.findAllBookingsByBookerId(any(State.class), anyLong())).thenReturn(bookingList);

        mockMvc.perform(get("/bookings/")
                        .header(HttpProperties.xSharerUserId, booker.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingDtoList.get(0).getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingDtoList.get(0).getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.id", is(bookingDtoList.get(0).getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[1].id", is(bookingDtoList.get(1).getId()), Long.class))
                .andExpect(jsonPath("$.[1].booker.id", is(bookingDtoList.get(1).getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[1].item.id", is(bookingDtoList.get(1).getItem().getId()), Long.class));

        verify(bookingServiceImplJpa, times(1)).findAllBookingsByBookerId(any(State.class), anyLong());
    }

    @Test
    public void testFindAllBookingsForAllItemsCurrentUser() throws Exception {
        Item anotherItem = new Item();
        anotherItem.setId(2L);
        anotherItem.setName("Test another name");
        anotherItem.setDescription("Test another description");
        anotherItem.setOwner(owner);
        anotherItem.setAvailable(true);

        Booking anotherBooking = new Booking();
        anotherBooking.setId(2L);
        anotherBooking.setStart(LocalDateTime.now().plusMinutes(1));
        anotherBooking.setEnd(LocalDateTime.now().plusDays(2));
        anotherBooking.setBooker(booker);
        anotherBooking.setItem(anotherItem);

        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        bookingList.add(anotherBooking);

        List<BookingDto> bookingDtoList = bookingList.stream()
                .map(BookingMapper::toBookingDto)
                .toList();

        when(bookingServiceImplJpa.findAllBookingsForAllItemsByOwnerId(any(State.class), anyLong())).thenReturn(bookingList);

        mockMvc.perform(get("/bookings/owner")
                        .header(HttpProperties.xSharerUserId, owner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingDtoList.get(0).getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingDtoList.get(0).getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.id", is(bookingDtoList.get(0).getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[1].id", is(bookingDtoList.get(1).getId()), Long.class))
                .andExpect(jsonPath("$.[1].booker.id", is(bookingDtoList.get(1).getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[1].item.id", is(bookingDtoList.get(1).getItem().getId()), Long.class));

        verify(bookingServiceImplJpa, times(1)).findAllBookingsForAllItemsByOwnerId(any(State.class), anyLong());
    }
}
