package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingExtendedDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(MockitoExtension.class)
class BookingServiceUnitTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    private BookingService bookingService;
    private User user;
    private Booking booking;
    private BookingExtendedDto bookingExtendedDto;
    private BookingCreateDto bookingCreatedDto;

    @BeforeEach
    void setUp() {
        user = new User(1, "harry", "mail@mail.ru");
        booking = new Booking(1,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                new Item(1, "name", "desr", true, user, null),
                new User(2, "harry2", "mail2@mail.ru"),
                Booking.BookingState.WAITING);
        bookingCreatedDto = new BookingCreateDto(1,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                1,
                2, Booking.BookingState.WAITING.name());
        bookingExtendedDto = BookingMapper.toBookingExtendedDto(booking);
        bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);
    }

    private BookingExtendedDto createBookingDto() {
        Mockito.when(userRepository.findById(Mockito.any()))
                .thenReturn(java.util.Optional.ofNullable(user));
        Mockito.when(bookingRepository.findBookingsByItem_IdIsAndStatusIsAndEndIsAfter(anyInt(), any(), any()))
                .thenReturn(List.of());
        Mockito.when(bookingRepository.save(any()))
                .thenReturn(booking);
        return bookingService.createBooking(bookingCreatedDto, 2);
    }


    @Test
    void createBooking() {
        BookingExtendedDto bookingExtendedDto = createBookingDto();
        Assertions.assertEquals(bookingExtendedDto.getId(), booking.getId());
        Assertions.assertEquals(bookingExtendedDto.getItem().getId(), booking.getItem().getId());
    }

    @Test
    void createBookingWithEmptyStartTime() {
        Mockito.when(userRepository.findById(Mockito.any()))
                .thenReturn(java.util.Optional.ofNullable(user));
        bookingCreatedDto.setStart(null);

        ValidationException ex = assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingCreatedDto, 2));
        Assertions.assertEquals("Заполните дату начала бронирования.", ex.getMessage());
    }

    @Test
    void createBookingWithEmptyEndTime() {
        Mockito.when(userRepository.findById(Mockito.any()))
                .thenReturn(java.util.Optional.ofNullable(user));
        bookingCreatedDto.setEnd(null);

        ValidationException ex = assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingCreatedDto, 2));
        Assertions.assertEquals("Заполните дату окончания бронирования.", ex.getMessage());
    }

    @Test
    void createBookingWithStartInPast() {
        Mockito.when(userRepository.findById(Mockito.any()))
                .thenReturn(java.util.Optional.ofNullable(user));
        bookingCreatedDto.setStart(LocalDateTime.now().minusDays(1));

        ValidationException ex = assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingCreatedDto, 2));
        Assertions.assertEquals("Некорректная дата начала бронирования.", ex.getMessage());
    }

    @Test
    void createBookingWithEmptyEndInPast() {
        Mockito.when(userRepository.findById(Mockito.any()))
                .thenReturn(java.util.Optional.ofNullable(user));
        bookingCreatedDto.setEnd(LocalDateTime.now().minusDays(1));

        NotFoundException ex = assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingCreatedDto, 2));
        Assertions.assertEquals("Некорректная дата окончания бронирования.", ex.getMessage());
    }

    @Test
    void createBookingWithNotAvailableItem() {
        booking.getItem().setAvailable(false);

        ValidationException ex = assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingCreatedDto, 2));
        Assertions.assertEquals("Товар с id " + booking.getItem().getId() + " не доступен до бронирования", ex.getMessage());
    }

    @Test
    void createBookingByItemOwner() {
        bookingCreatedDto.setBooker(booking.getItem().getOwner().getId());
        NotFoundException ex = assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingCreatedDto, bookingCreatedDto.getBooker()));
        Assertions.assertEquals("Not found item with id = 1", ex.getMessage());
    }

    @Test
    void createBookingBusyItem() {
        Mockito.when(userRepository.findById(Mockito.any()))
                .thenReturn(java.util.Optional.ofNullable(user));
        Mockito.when(bookingRepository.findBookingsByItem_IdIsAndStatusIsAndEndIsAfter(anyInt(), any(), any()))
                .thenReturn(List.of(booking));
        NotFoundException ex = assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingCreatedDto, 2));
        Assertions.assertEquals("Not found item with id = 1 ", ex.getMessage());
    }

    @Test
    void approveBooking() {
        Booking approved = new Booking(booking.getId(), booking.getStart(), booking.getEnd(), booking.getItem(), booking.getBooker(), Booking.BookingState.APPROVED);
        BookingExtendedDto bookingExtendedDto = createBookingDto();
        Mockito.when(bookingRepository.findById(anyInt()))
                .thenReturn(java.util.Optional.ofNullable(booking));
        Mockito.when(bookingRepository.save(any()))
                .thenReturn(approved);
        BookingExtendedDto returned = bookingService.approveBooking(booking.getId(), true, user.getId());
        Assertions.assertEquals(returned.getId(), approved.getId());
        Assertions.assertEquals(returned.getStatus(), approved.getStatus().name());

    }

    @Test
    void approveBookingNotItemOwner() {
        Mockito.when(bookingRepository.findById(anyInt()))
                .thenReturn(java.util.Optional.ofNullable(booking));
        ValidationException ex = assertThrows(ValidationException.class, () -> bookingService.approveBooking(booking.getId(), true, 3));
        Assertions.assertEquals("Статус бронирования не может быть обновлен", ex.getMessage());
    }

    @Test
    void approveBookingByBooker() {
        Mockito.when(bookingRepository.findById(anyInt()))
                .thenReturn(java.util.Optional.ofNullable(booking));

        NotFoundException ex = assertThrows(NotFoundException.class, () -> bookingService.approveBooking(booking.getId(), true, booking.getBooker().getId()));
        Assertions.assertEquals("Не существует доступного для обновления бронирования для пользователя с id = " + booking.getBooker().getId() + ".", ex.getMessage());
    }

    @Test
    void approveApprovedBooking() {
        booking.setStatus(Booking.BookingState.APPROVED);
        Mockito.when(bookingRepository.findById(anyInt()))
                .thenReturn(java.util.Optional.ofNullable(booking));
        ValidationException ex = assertThrows(ValidationException.class, () -> bookingService.approveBooking(booking.getId(), true, user.getId()));
        Assertions.assertEquals("Статус бронирования не может быть обновлен", ex.getMessage());
    }

    @Test
    void getBooking() {
        BookingExtendedDto bookingExtendedDto = createBookingDto();
        Mockito.when(bookingRepository.findById(anyInt()))
                .thenReturn(java.util.Optional.ofNullable(booking));
        BookingExtendedDto returned = bookingService.getBooking(bookingExtendedDto.getId(), user.getId());
        Assertions.assertEquals(returned.getId(), booking.getId());
        Assertions.assertEquals(returned.getItem().getId(), booking.getItem().getId());
    }

    @Test
    void getNotFoundBooking() {
        Mockito.when(bookingRepository.findById(anyInt()))
                .thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> bookingService.getBooking(99, 99));
    }

    @Test
    void getBookingByOtherUser() {
        Mockito.when(bookingRepository.findById(anyInt()))
                .thenReturn(java.util.Optional.ofNullable(booking));
        assertThrows(NotFoundException.class, () -> bookingService.getBooking(booking.getId(), 99));
    }

    @Test
    void getBookings() {
        createBookingDto();
        Mockito.when(bookingRepository.findBookingsByBookerIsOrderByStartDesc(any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingExtendedDto> returned = bookingService.getBookings(user.getId(), null, null, null);
        Assertions.assertEquals(returned.size(), 1);
        Assertions.assertEquals(returned.get(0).getId(), booking.getId());
    }


    @Test
    void getBookingsWithIncorrectStartPaginationValue() {
        ValidationException ex = assertThrows(ValidationException.class, () -> bookingService.getBookings(user.getId(), "Hello", -1, 20));
        Assertions.assertEquals("Некорректное значение для постраничной загрузки", ex.getMessage());
    }

    @Test
    void getBookingsWithIncorrectEndPaginationValue() {
        ValidationException ex = assertThrows(ValidationException.class, () -> bookingService.getBookings(user.getId(), "Hello", 0, 0));
        Assertions.assertEquals("Некорректное значение для постраничной загрузки", ex.getMessage());
    }


    @Test
    void getBookingsWithNotSupportedState() {
        createBookingDto();
        ValidationException ex = assertThrows(ValidationException.class, () -> bookingService.getBookings(user.getId(), "Hello", null, null));
        Assertions.assertEquals("Unknown state: Hello", ex.getMessage());
    }

    @Test
    void getBookingsWithAllState() {
        createBookingDto();
        Mockito.when(bookingRepository.findBookingsByBookerIsOrderByStartDesc(any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingExtendedDto> returned = bookingService.getBookings(user.getId(), Booking.TimeBookingState.ALL.name(), null, null);
        Assertions.assertEquals(returned.size(), 1);
        Assertions.assertEquals(returned.get(0).getId(), booking.getId());
    }

    @Test
    void getBookingsWithPastState() {
        createBookingDto();
        Mockito.when(bookingRepository.findBookingsByBookerIsAndEndBeforeOrderByStartDesc(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingExtendedDto> returned = bookingService.getBookings(user.getId(), Booking.TimeBookingState.PAST.name(), null, null);
        Assertions.assertEquals(returned.size(), 1);
        Assertions.assertEquals(returned.get(0).getId(), booking.getId());
    }

    @Test
    void getBookingsWithFutureState() {
        createBookingDto();
        Mockito.when(bookingRepository.findBookingsByBookerIsAndStartIsAfterOrderByStartDesc(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingExtendedDto> returned = bookingService.getBookings(user.getId(), Booking.TimeBookingState.FUTURE.name(), null, null);
        Assertions.assertEquals(returned.size(), 1);
        Assertions.assertEquals(returned.get(0).getId(), booking.getId());
    }

    @Test
    void getBookingsWithCurrentState() {
        createBookingDto();
        Mockito.when(bookingRepository.findBookingsByBookerIsAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingExtendedDto> returned = bookingService.getBookings(user.getId(), Booking.TimeBookingState.CURRENT.name(), null, null);
        Assertions.assertEquals(returned.size(), 1);
        Assertions.assertEquals(returned.get(0).getId(), booking.getId());
    }

    @Test
    void getBookingsWithRejectState() {
        createBookingDto();
        Mockito.when(bookingRepository.findBookingsByBookerIsAndStatusIsOrderByStartDesc(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingExtendedDto> returned = bookingService.getBookings(user.getId(), Booking.BookingState.REJECTED.name(), null, null);
        Assertions.assertEquals(returned.size(), 1);
        Assertions.assertEquals(returned.get(0).getId(), booking.getId());
    }

    @Test
    void getBookingsWithCancelStateWithEmptyResult() {
        createBookingDto();
        Mockito.when(bookingRepository.findBookingsByBookerIsAndStatusIsOrderByStartDesc(any(), any(), any()))
                .thenReturn(Page.empty());
        List<BookingExtendedDto> returned = bookingService.getBookings(user.getId(), Booking.BookingState.CANCELED.name(), null, null);
        Assertions.assertEquals(returned.size(), 0);
    }

    @Test
    void getBookingsWithEmptyResult() {
        createBookingDto();
        Mockito.when(bookingRepository.findBookingsByBookerIsAndStartIsAfterOrderByStartDesc(any(), any(), any()))
                .thenReturn(Page.empty());
        List<BookingExtendedDto> returned = bookingService.getBookings(user.getId(), Booking.TimeBookingState.FUTURE.name(), null, null);
        Assertions.assertEquals(returned.size(), 0);
    }

    @Test
    void getOwnersBookings() {
        createBookingDto();
        Mockito.when(bookingRepository.findBookingsByItemOwnerIsOrderByStartDesc(any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingExtendedDto> returned = bookingService.getOwnersBookings(user.getId(), null, null, null);
        Assertions.assertEquals(returned.size(), 1);
        Assertions.assertEquals(returned.get(0).getId(), booking.getId());
    }

    @Test
    void getOwnerBookingsWithAllState() {
        createBookingDto();
        Mockito.when(bookingRepository.findBookingsByItemOwnerIsOrderByStartDesc(any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingExtendedDto> returned = bookingService.getOwnersBookings(user.getId(), Booking.TimeBookingState.ALL.name(), null, null);
        Assertions.assertEquals(returned.size(), 1);
        Assertions.assertEquals(returned.get(0).getId(), booking.getId());
    }

    @Test
    void getOwnersBookingsWithPastState() {
        createBookingDto();
        Mockito.when(bookingRepository.findBookingsByItemOwnerAndEndBeforeOrderByStartDesc(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingExtendedDto> returned = bookingService.getOwnersBookings(user.getId(), Booking.TimeBookingState.PAST.name(), null, null);
        Assertions.assertEquals(returned.size(), 1);
        Assertions.assertEquals(returned.get(0).getId(), booking.getId());
    }

    @Test
    void getOwnersBookingsWithCurrentState() {
        createBookingDto();
        Mockito.when(bookingRepository.findBookingsByItemOwnerIsAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingExtendedDto> returned = bookingService.getOwnersBookings(user.getId(), Booking.TimeBookingState.CURRENT.name(), null, null);
        Assertions.assertEquals(returned.size(), 1);
        Assertions.assertEquals(returned.get(0).getId(), booking.getId());
    }

    @Test
    void getOwnersBookingsWithFutureState() {
        createBookingDto();
        Mockito.when(bookingRepository.findBookingsByItemOwnerAndStartAfterOrderByStartDesc(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingExtendedDto> returned = bookingService.getOwnersBookings(user.getId(), Booking.TimeBookingState.FUTURE.name(), null, null);
        Assertions.assertEquals(returned.size(), 1);
        Assertions.assertEquals(returned.get(0).getId(), booking.getId());
    }

    @Test
    void getOwnersBookingsWithRejectState() {
        createBookingDto();
        Mockito.when(bookingRepository.findBookingsByItemOwnerIsAndStatusIsOrderByStartDesc(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingExtendedDto> returned = bookingService.getOwnersBookings(user.getId(), Booking.BookingState.REJECTED.name(), null, null);
        Assertions.assertEquals(returned.size(), 1);
        Assertions.assertEquals(returned.get(0).getId(), booking.getId());
    }

    @Test
    void getOwnersBookingsWithNotSupportedState() {
        createBookingDto();
        ValidationException ex = assertThrows(ValidationException.class, () -> bookingService.getOwnersBookings(user.getId(), "Hello", null, null));
        Assertions.assertEquals("Unknown state: Hello", ex.getMessage());
    }


    @Test
    void getBookingsByItem() {
        Mockito.when(bookingRepository.findBookingsByItem_IdAndItem_Owner_IdIsOrderByStart(anyInt(), anyInt()))
                .thenReturn(List.of(booking));

        List<BookingExtendedDto> returned = bookingService.getBookingsByItem(1, 2);
        Assertions.assertEquals(returned.size(), 1);
        Assertions.assertEquals(returned.get(0).getId(), booking.getId());
    }

    @Test
    void getBookingsByItemWithEmptyResult() {
        Mockito.when(bookingRepository.findBookingsByItem_IdAndItem_Owner_IdIsOrderByStart(anyInt(), anyInt()))
                .thenReturn(List.of());

        List<BookingExtendedDto> returned = bookingService.getBookingsByItem(1, 2);
        Assertions.assertEquals(returned.size(), 0);
    }
}