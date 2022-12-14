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
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public Booking createBooking(Booking booking, Integer bookerId) {
        Item item = itemService.getItem(booking.getId());
        if (!item.getAvailable()) {
            throw new ValidationException("Товар с id " + item.getId() + " не доступен до бронирования");
        }
        User booker = userService.getUser(bookerId);
        booking.setBooker(booker);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking updateBooking(Booking booking, Integer bookerId) {
        log.warn("#################################### update booking " + booking + " " + bookerId);
        return booking;
    }

    @Override
    public Booking approveBooking(Booking booking, boolean approved, Integer userId) {
        log.warn("#################################### approveBooking " + booking + " " + userId + " " + approved);

        return booking;
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
        return null;
    }

    @Override
    public List<Booking> getOwnerBookings(Integer userId, String state) {
        return null;
    }
}
