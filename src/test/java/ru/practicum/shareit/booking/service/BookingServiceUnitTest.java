package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingExtendedDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(MockitoExtension.class)
class BookingServiceUnitTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserService userService;
    private BookingService bookingService;
    private UserDto userDto;
    private Booking booking;
    private BookingExtendedDto bookingExtendedDto;
    private BookingCreateDto bookingCreatedDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(1, "harry", "mail@mail.ru");
        booking = new Booking(1,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                new Item(1, "name", "desr", true, UserMapper.toUser(userDto), null),
                UserMapper.toUser(userDto),
                Booking.BookingState.WAITING);
        bookingCreatedDto = new BookingCreateDto(1,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                1,
                2, Booking.BookingState.WAITING.name());
        bookingExtendedDto = BookingMapper.toBookingExtendedDto(booking);
        bookingService = new BookingServiceImpl(bookingRepository, userService);
    }

    private BookingExtendedDto createBookingDto() {
        Mockito.when(userService.getUser(Mockito.any()))
                .thenReturn(userDto);
        Mockito.when(bookingRepository.findBookingsByItem_IdIsAndStatusIsAndEndIsAfter(anyInt(), any(), any()))
                .thenReturn(List.of());
        Mockito.when(bookingRepository.save(any()))
                .thenReturn(booking);
        return bookingService.createBooking(bookingCreatedDto, ItemMapper.toItemExtendedDto(booking.getItem(), null, null, List.of()), 2);
    }

    @Test
    void getBooking() {
        BookingExtendedDto bookingExtendedDto = createBookingDto();
        Mockito.when(bookingRepository.findById(anyInt()))
                .thenReturn(java.util.Optional.ofNullable(booking));
        BookingExtendedDto returned = bookingService.getBooking(bookingExtendedDto.getId(), userDto.getId());
        Assertions.assertEquals(returned.getId(), booking.getId());
        Assertions.assertEquals(returned.getItem().getId(), booking.getItem().getId());
    }

    @Test
    void getBookings() {
        createBookingDto();
        Mockito.when(bookingRepository.findBookingsByBookerIsOrderByStartDesc(any()))
                .thenReturn(List.of(booking));
        List<BookingExtendedDto> returned = bookingService.getBookings(userDto.getId(), null, null, null);
        Assertions.assertEquals(returned.size(), 1);
        Assertions.assertEquals(returned.get(0).getId(), booking.getId());
    }

    @Test
    void getBookingsWithEmptyResult() {
        createBookingDto();
        Mockito.when(bookingRepository.findBookingsByBookerIsAndStartIsAfterOrderByStartDesc(any(), any()))
                .thenReturn(List.of());
        List<BookingExtendedDto> returned = bookingService.getBookings(userDto.getId(), Booking.TimeBookingState.FUTURE.name(), null, null);
        Assertions.assertEquals(returned.size(), 0);
    }

    @Test
    void createBooking() {
        BookingExtendedDto bookingExtendedDto = createBookingDto();
        Assertions.assertEquals(bookingExtendedDto.getId(), booking.getId());
        Assertions.assertEquals(bookingExtendedDto.getItem().getId(), booking.getItem().getId());
    }

    @Test
    void getOwnersBookings() {
        createBookingDto();
        Mockito.when(bookingRepository.findBookingsByItemOwnerIsOrderByStartDesc(any()))
                .thenReturn(List.of(booking));
        List<BookingExtendedDto> returned = bookingService.getOwnersBookings(userDto.getId(), null, null, null);
        Assertions.assertEquals(returned.size(), 1);
        Assertions.assertEquals(returned.get(0).getId(), booking.getId());
    }

}