package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.ConflictException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public Booking createBooking(Booking booking, Integer itemId, Integer bookerId) {
        Item item = itemService.getItem(itemId);
        if (!item.getAvailable()) {
            throw new ValidationException("Товар с id " + item.getId() + " не доступен до бронирования");
        }
        User booker = userService.getUser(bookerId);

        validationBooking(booking);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Booking.BookingState.WAITING);

        try {
            return bookingRepository.save(booking);
        } catch (Exception e) {
           log.warn("!!!!!!!!!!!!!!!!!!!!!!!!!!! " + e);
        }

        return null;
    }

    @Override
    public Booking updateBooking(Booking booking, Integer bookerId) {
        log.warn("#################################### update booking " + booking + " " + bookerId);
        return booking;
    }

    @Override
    public Booking approveBooking(Integer bookingId, boolean approved, Integer userId) {
        Booking booking = getBooking(bookingId, userId);
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException("Бронирование может подтвердить только владелец");
        }
        booking.setStatus(approved ? Booking.BookingState.APPROVED : Booking.BookingState.REJECTED);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBooking(Integer id, Integer userId) {
        return bookingRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Бронирования с id = " + id + " не существует."));
    }

    @Override
    public void deleteBooking(Integer id) {
        bookingRepository.deleteById(id);
    }

    @Override
    public List<Booking> getBookings(Integer bookerId, String state) {
        User booker = userService.getUser(bookerId);
        Predicate<Booking.BookingState> predicate = status -> status.name().equals(state);

        List<Booking.BookingState> bookingStates = Arrays.asList(Booking.BookingState.values());
        if(state != null && state.equals("ALL")) {
            bookingStates = bookingStates.stream().filter(predicate).collect(Collectors.toList());
        }
        return bookingRepository.findBookingsByBookerIsAndStatusIn(booker, bookingStates);
    }

    @Override
    public List<Booking> getOwnerBookings(Integer userId, String state) {
        return null;
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
