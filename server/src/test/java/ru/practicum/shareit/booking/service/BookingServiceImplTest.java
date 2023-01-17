package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingExtendedDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {
    private final EntityManager entityManager;
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private UserDto itemOwner;
    private ItemDto itemDto;
    private BookingExtendedDto bookingExtendedDto;

    @BeforeEach
    void setUp() {
        itemOwner = userService.createUser(new UserDto(null, "Harry", "mail@mail.ru"));
        UserDto bookerOwner = userService.createUser(new UserDto(null, "Booker", "booker@mail.ru"));
        itemDto = itemService.createItem(new ItemDto(null,
                "Item",
                "Description",
                true, itemOwner.getId(), null), itemOwner.getId());

        ItemExtendedDto itemExtendedDto = new ItemExtendedDto(itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                true, itemOwner.getId(), null, null, null, List.of());
        bookingExtendedDto = bookingService.createBooking(new BookingCreateDto(null,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                itemDto.getId(),
                bookerOwner.getId(),
                Booking.BookingState.WAITING.name()), bookerOwner.getId());
    }

    @Test
    void createBooking() {
        Booking booking = entityManager.createQuery("Select b from Booking b", Booking.class).getSingleResult();

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getBooker().getId(), equalTo(bookingExtendedDto.getBooker().getId()));
        assertThat(booking.getItem().getId(), equalTo(bookingExtendedDto.getItem().getId()));
    }

    @Test
    void approveBooking() {
        BookingExtendedDto returned = bookingService.approveBooking(bookingExtendedDto.getId(), true, bookingExtendedDto.getItem().getOwnerId());
        Booking booking = entityManager.createQuery("Select b from Booking b", Booking.class).getSingleResult();

        assertThat(booking.getId(), equalTo(returned.getId()));
        assertThat(booking.getBooker().getId(), equalTo(returned.getBooker().getId()));
        assertThat(booking.getItem().getId(), equalTo(returned.getItem().getId()));
        assertThat(booking.getStatus().name(), equalTo(returned.getStatus()));

    }

    @Test
    void getBooking() {
        BookingExtendedDto returned = bookingService.getBooking(bookingExtendedDto.getId(), bookingExtendedDto.getBooker().getId());
        Booking booking = entityManager.createQuery("Select b from Booking b where b.id = :id and b.booker.id = :bookerId", Booking.class)
                .setParameter("id", bookingExtendedDto.getId())
                .setParameter("bookerId", bookingExtendedDto.getBooker().getId())
                .getSingleResult();
        assertThat(returned.getId(), equalTo(booking.getId()));
        assertThat(returned.getItem().getId(), equalTo(booking.getItem().getId()));
        assertThat(returned.getStart(), equalTo(booking.getStart()));
    }

    @Test
    void getBookings() {
        List<BookingExtendedDto> returned = bookingService.getBookings(bookingExtendedDto.getBooker().getId(), null, null, null);
        List<Booking> booking = entityManager.createQuery("Select b from Booking b where b.booker.id = :id", Booking.class)
                .setParameter("id", bookingExtendedDto.getBooker().getId())
                .getResultList();
        assertThat(returned.size(), equalTo(booking.size()));
        assertThat(returned.get(0).getId(), equalTo(booking.get(0).getId()));
    }

    @Test
    void getBookingsWithEmptyResult() {
        List<BookingExtendedDto> returnedApproved = bookingService.getBookings(bookingExtendedDto.getBooker().getId(), Booking.BookingState.APPROVED.name(), null, null);
        List<Booking> bookingApproved = entityManager.createQuery("Select b from Booking b where b.booker.id = :id and b.status = :status", Booking.class)
                .setParameter("id", bookingExtendedDto.getBooker().getId())
                .setParameter("status", Booking.BookingState.APPROVED)
                .getResultList();
        assertThat(returnedApproved.size(), equalTo(bookingApproved.size()));
        assertThat(returnedApproved.size(), equalTo(0));
    }

    @Test
    void getOwnersBookings() {
        List<BookingExtendedDto> returned = bookingService.getOwnersBookings(itemOwner.getId(), null, null, null);
        List<Booking> booking = entityManager.createQuery("Select b from Booking b inner join b.item i where i.owner.id = :id", Booking.class)
                .setParameter("id", itemOwner.getId())
                .getResultList();
        assertThat(returned.size(), equalTo(booking.size()));
        assertThat(returned.get(0).getId(), equalTo(booking.get(0).getId()));
    }

    @Test
    void getOwnerBookingsWithEmptyResult() {
        List<BookingExtendedDto> returnedApproved = bookingService.getOwnersBookings(itemOwner.getId(), Booking.BookingState.APPROVED.name(), null, null);
        List<Booking> bookingApproved = entityManager.createQuery("Select b from Booking b inner join b.item i where i.owner.id = :id and b.status = :status", Booking.class)
                .setParameter("id", itemOwner.getId())
                .setParameter("status", Booking.BookingState.APPROVED)
                .getResultList();
        assertThat(returnedApproved.size(), equalTo(bookingApproved.size()));
        assertThat(returnedApproved.size(), equalTo(0));
    }


    @Test
    void getBookingsByItem() {
        List<BookingExtendedDto> returned = bookingService.getBookingsByItem(itemDto.getId(), itemOwner.getId());
        List<Booking> bookings = entityManager.createQuery("Select b from Booking b inner join b.item i where i.owner.id = :ownerId and i.id = :itemId", Booking.class)
                .setParameter("ownerId", itemOwner.getId())
                .setParameter("itemId", itemDto.getId())
                .getResultList();
        assertThat(returned.size(), equalTo(bookings.size()));
        assertThat(returned.get(0).getId(), equalTo(bookings.get(0).getId()));
    }
}