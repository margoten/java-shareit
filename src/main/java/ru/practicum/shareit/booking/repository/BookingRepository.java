package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
//    @Query(" select i from Item i " +
//            "where i.available = TRUE and (upper(i.name) like upper(concat('%', ?1, '%')) " +
//            " or upper(i.description) like upper(concat('%', ?1, '%')))")
//    List<Booking> search(String text);
    List<Booking> findBookingsByBookerIsAndStatusIn(User booker, List<Booking.BookingState> bookingStates);

    List<Booking> findBookingsBy
}
