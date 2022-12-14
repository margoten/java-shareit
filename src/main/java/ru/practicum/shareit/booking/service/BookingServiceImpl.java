package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
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
        return null;
    }

    @Override
    public Booking updateBooking(Booking booking, Integer bookerId) {
        return null;
    }

    @Override
    public Booking getBooking(Integer id) {
        return null;
    }

    @Override
    public void deleteBooking(Integer id) {

    }

    @Override
    public List<Booking> getBookings(Integer bookerId) {
        return null;
    }
}
