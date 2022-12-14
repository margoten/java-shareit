package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface BookingService {
    Booking createBooking(Booking booking, Integer bookerId);

    Booking updateBooking(Booking booking, Integer bookerId);

    Booking getBooking(Integer id);

    void deleteBooking(Integer id);

    List<Booking> getBookings(Integer bookerId);

}
