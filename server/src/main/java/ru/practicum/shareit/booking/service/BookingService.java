package ru.practicum.shareit.booking.service;

import lombok.NonNull;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingExtendedDto;

import java.util.List;

public interface BookingService {
    BookingExtendedDto getBooking(Integer id, Integer userId);

    List<BookingExtendedDto> getBookings(Integer bookerId, @NonNull String state, @NonNull Integer from, @NonNull Integer size);

    BookingExtendedDto createBooking(BookingCreateDto booking, Integer bookerId);

    BookingExtendedDto approveBooking(Integer bookingId, boolean approved, Integer userId);

    List<BookingExtendedDto> getOwnersBookings(Integer userId, @NonNull String state, @NonNull Integer from, @NonNull Integer size);

    List<BookingExtendedDto> getBookingsByItem(Integer itemId, Integer userId);


}
