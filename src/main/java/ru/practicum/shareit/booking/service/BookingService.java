package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface BookingService {
    Booking getBooking(Integer id, Integer userId);

    List<Booking> getBookings(Integer bookerId, String state);

    Booking getLastItemBooking(Item item, Integer bookerId);

    Booking getNextItemBooking(Item item, Integer bookerId);

    Booking createBooking(Booking booking, Item item, Integer bookerId);

    Booking approveBooking(Integer bookingId, boolean approved, Integer userId);

    List<Booking> getOwnersBookings(Integer userId, String state);

    Booking getLastItemBooking(List<Booking> bookings);

    Booking getNextItemBooking(List<Booking> bookings);


}
