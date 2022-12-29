package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingExtendedDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface BookingService {
    BookingExtendedDto getBooking(Integer id, Integer userId);

    List<BookingExtendedDto> getBookings(Integer bookerId, String state);

    BookingExtendedDto createBooking(BookingCreateDto booking, ItemExtendedDto itemDto, Integer bookerId);

    BookingExtendedDto approveBooking(Integer bookingId, boolean approved, Integer userId);

    List<BookingExtendedDto> getOwnersBookings(Integer userId, String state);

    List<BookingExtendedDto> getBookingsByItem(Integer itemId);



}
