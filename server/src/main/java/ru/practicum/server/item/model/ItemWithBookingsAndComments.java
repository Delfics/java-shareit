package ru.practicum.server.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Data
public class ItemWithBookingsAndComments {
     Item item;
     LocalDateTime nextBookingTime;
     LocalDateTime lastBookingTime;
     List<String> comments;

     public ItemWithBookingsAndComments() {
     }

     public ItemWithBookingsAndComments(Item item, LocalDateTime nextBookingTime, LocalDateTime lastBookingTime) {
          this.item = item;
          this.nextBookingTime = nextBookingTime;
          this.lastBookingTime = lastBookingTime;
     }
}
