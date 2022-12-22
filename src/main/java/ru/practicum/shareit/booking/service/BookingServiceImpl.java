package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;

    @Override
    public Booking createBooking(Booking booking, Item item, Integer bookerId) {
        if (!item.getAvailable()) {
            throw new ValidationException("Товар с id " + item.getId() + " не доступен до бронирования");
        }

        if (item.getOwner().getId().equals(bookerId)) {
            throw new NotFoundException("Товар с id " + item.getId() + " не может быть заброванирован владельцем");
        }
        User booker = userService.getUser(bookerId);

        validationBooking(booking);

        List<Booking> bookings = bookingRepository.findBookingsByItemIsAndStatusIsAndEndIsAfter(item, Booking.BookingState.APPROVED, booking.getStart());
        if (!bookings.isEmpty()) {
            throw new NotFoundException("Недоступный товар для бронирования " + item.getName());
        }
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Booking.BookingState.WAITING);

        return bookingRepository.save(booking);
    }

    @Override
    public Booking approveBooking(Integer bookingId, boolean approved, Integer userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Бронирования с id = " + bookingId + " не существует."));
        if (booking.getBooker().getId().equals(userId)) {
            throw new NotFoundException("Не существует доступного для обновления бронирования для пользователя с id = " + userId + ".");

        }
        if (!booking.getItem().getOwner().getId().equals(userId) || !booking.getStatus().equals(Booking.BookingState.WAITING)) {
            throw new ValidationException("Стастус бронирования не может быть обновлен");
        }
        booking.setStatus(approved ? Booking.BookingState.APPROVED : Booking.BookingState.REJECTED);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBooking(Integer id, Integer userId) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Бронирования с id = " + id + " не существует."));
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Не существует доступного бронирования для пользователя с id = " + userId + ".");
        }
        return booking;
    }

    @Override
    public List<Booking> getBookings(Integer bookerId, String state) {
        User booker = userService.getUser(bookerId);

        if (state == null || state.equals(Booking.TimeBookingState.ALL.name())) {
            return bookingRepository.findBookingsByBookerIsOrderByStartDesc(booker);
        }
        if (state.equals(Booking.TimeBookingState.FUTURE.name())) {
            return bookingRepository.findBookingsByBookerIsAndStartIsAfterOrderByStartDesc(booker, LocalDateTime.now());
        }
        if (state.equals(Booking.TimeBookingState.CURRENT.name())) {
            return bookingRepository.findBookingsByBookerIsAndStartBeforeAndEndAfterOrderByStartDesc(booker, LocalDateTime.now(), LocalDateTime.now());
        }

        if (state.equals(Booking.TimeBookingState.PAST.name())) {
            return bookingRepository.findBookingsByBookerIsAndEndBeforeOrderByStartDesc(booker, LocalDateTime.now());
        }

        if (Arrays.stream(Booking.BookingState.values()).anyMatch(bookingState -> bookingState.name().equals(state))) {
            return bookingRepository.findBookingsByBookerIsAndStatusIsOrderByStartDesc(booker, Booking.BookingState.valueOf(state));
        }

        throw new ValidationException("Unknown state: " + state);

    }

    @Override
    public List<Booking> getOwnersBookings(Integer userId, String state) {
        User owner = userService.getUser(userId);

        if (state == null || state.equals(Booking.TimeBookingState.ALL.name())) {
            return bookingRepository.findBookingsByOwnerOrderByStartDesc(owner);
        }
        if (state.equals(Booking.TimeBookingState.FUTURE.name())) {
            return bookingRepository.findBookingsByOwnerAndStartAfterOrderByStartDesc(owner, LocalDateTime.now());
        }
        if (state.equals(Booking.TimeBookingState.CURRENT.name())) {
            return bookingRepository.findBookingsByOwnerIsAndStartBeforeAndEndAfterOrderByStartDesc(owner, LocalDateTime.now(), LocalDateTime.now());
        }

        if (state.equals(Booking.TimeBookingState.PAST.name())) {
            return bookingRepository.findBookingsByOwnerAndEndBeforeOrderByStartDesc(owner, LocalDateTime.now());
        }
        if (Arrays.stream(Booking.BookingState.values()).anyMatch(bookingState -> bookingState.name().equals(state))) {
            return bookingRepository.findBookingsByOwnerIsAndStatusIsOrderByStartDesc(owner, Booking.BookingState.valueOf(state));
        }
        throw new ValidationException("Unknown state: " + state);
    }

    @Override
    public Booking getLastItemBooking(Item item, Integer bookerId) {
        User user = userService.getUser(bookerId);
        return bookingRepository.findFirstByOwnerIsAndEndBeforeOrderByStartDesc(item, user, LocalDateTime.now()).orElse(null);
    }

    @Override
    public Booking getNextItemBooking(Item item, Integer bookerId) {
        User user = userService.getUser(bookerId);
        return bookingRepository.findFirstByOwnerIsAndStartAfterOrderByStartAsc(item, user, LocalDateTime.now()).orElse(null);
    }

    private void validationBooking(Booking booking) {

        if (booking.getStart() == null) {
            throw new ValidationException("Заполните дату начала бронирования.");
        }
        if (booking.getEnd() == null) {
            throw new ValidationException("Заполните дату окончания бронирования.");
        }

        if (booking.getStart().toLocalDate().isBefore(LocalDate.now())) {
            throw new ValidationException("Некорректная дата начала бронирования.");
        }
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getEnd().toLocalDate().isBefore(LocalDate.now())) {
            throw new ValidationException("Некорректная дата окончания бронирования.");
        }
    }
}
