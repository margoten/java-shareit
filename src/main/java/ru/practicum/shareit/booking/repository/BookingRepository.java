package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    //region Методы запросов бронирования для автора бронирования
    List<Booking> findBookingsByItem_IdIsAndStatusIsAndEndIsAfter(Integer itemId, Booking.BookingState bookingState, LocalDateTime localDateTime);

    List<Booking> findBookingsByBookerIsOrderByStartDesc(User booker);

    List<Booking> findBookingsByBookerIsAndStatusIsOrderByStartDesc(User booker, Booking.BookingState bookingState);

    List<Booking> findBookingsByBookerIsAndStartIsAfterOrderByStartDesc(User booker, LocalDateTime localDateTime);

    List<Booking> findBookingsByBookerIsAndEndBeforeOrderByStartDesc(User booker, LocalDateTime localDateTime);

    List<Booking> findBookingsByBookerIsAndStartBeforeAndEndAfterOrderByStartDesc(User booker, LocalDateTime startDateTime, LocalDateTime endDateTime);
    //endregion

    //region Методы запросов бронирования для владельца товара
    List<Booking> findBookingsByItemOwnerIsOrderByStartDesc(User owner);

    List<Booking> findBookingsByItemOwnerAndStartAfterOrderByStartDesc(User owner, LocalDateTime localDateTime);

    List<Booking> findBookingsByItemOwnerAndEndBeforeOrderByStartDesc(User owner, LocalDateTime localDateTime);

    List<Booking> findBookingsByItemOwnerIsAndStatusIsOrderByStartDesc(User owner, Booking.BookingState bookingState);

    List<Booking> findBookingsByItemOwnerIsAndStartBeforeAndEndAfterOrderByStartDesc(User owner, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<Booking> findBookingsByItem_IdAndItem_Owner_IdIsOrderByStart(Integer itemId, Integer userId);

    //endregion
}
