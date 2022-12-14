package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {
    private int id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final Item item;
    private final User booker;
    private BookingState status;


    static enum BookingState {
        WAITING(0),
        APPROVED(1),
        REJECTED(2),
        CANCELED(3);

        private final int id;

        BookingState(int id) {

            this.id = id;
        }
    }
}
