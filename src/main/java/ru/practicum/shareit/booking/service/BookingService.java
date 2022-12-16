package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.Booking;

import java.util.List;

public interface BookingService {
    Booking createBooking(Booking booking, Integer itemId, Integer bookerId);

    Booking updateBooking(Booking booking, Integer bookerId);

    Booking getBooking(Integer id, Integer userId);

    void deleteBooking(Integer id);

    List<Booking> getBookings(Integer bookerId, String state);

    Booking approveBooking(Integer bookingId, boolean approved, Integer userId);

    List<Booking> getOwnerBookings(Integer userId, String state);
}
