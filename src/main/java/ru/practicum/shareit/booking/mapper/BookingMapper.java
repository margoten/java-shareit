package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingExtendedDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

public class BookingMapper {
    public static BookingExtendedDto toBookingExtendedDto(Booking booking) {
        return new BookingExtendedDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem() == null ? null : ItemMapper.toItemDto(booking.getItem()),
                booking.getBooker() == null ? null : UserMapper.toUserDto(booking.getBooker()),
                booking.getStatus().name()
        );
    }

    public static Booking toBooking(BookingCreateDto bookingDto) {
        return Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();
    }
}
