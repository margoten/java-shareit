package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    //region Методы запросов бронирования для автора бронирования
    List<Booking> findBookingsByItem_IdIsAndStatusIsAndEndIsAfter(Integer itemId, Booking.BookingState bookingState, LocalDateTime localDateTime);

    List<Booking> findBookingsByBookerIsOrderByStartDesc(User booker);

    Page<Booking> findBookingsByBookerIsOrderByStartDesc(User booker, Pageable pageable);

    List<Booking> findBookingsByBookerIsAndStatusIsOrderByStartDesc(User booker, Booking.BookingState bookingState);

    Page<Booking> findBookingsByBookerIsAndStatusIsOrderByStartDesc(User booker, Booking.BookingState bookingState, Pageable pageable);

    List<Booking> findBookingsByBookerIsAndStartIsAfterOrderByStartDesc(User booker, LocalDateTime localDateTime);

    Page<Booking> findBookingsByBookerIsAndStartIsAfterOrderByStartDesc(User booker, LocalDateTime localDateTime, Pageable pageable);


    List<Booking> findBookingsByBookerIsAndEndBeforeOrderByStartDesc(User booker, LocalDateTime localDateTime);

    Page<Booking> findBookingsByBookerIsAndEndBeforeOrderByStartDesc(User booker, LocalDateTime localDateTime, Pageable pageable);

    List<Booking> findBookingsByBookerIsAndStartBeforeAndEndAfterOrderByStartDesc(User booker, LocalDateTime startDateTime, LocalDateTime endDateTime);

    Page<Booking> findBookingsByBookerIsAndStartBeforeAndEndAfterOrderByStartDesc(User booker, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);
    //endregion

    //region Методы запросов бронирования для владельца товара
    List<Booking> findBookingsByItemOwnerIsOrderByStartDesc(User owner);

    Page<Booking> findBookingsByItemOwnerIsOrderByStartDesc(User owner, Pageable pageable);

    List<Booking> findBookingsByItemOwnerAndStartAfterOrderByStartDesc(User owner, LocalDateTime localDateTime);

    Page<Booking> findBookingsByItemOwnerAndStartAfterOrderByStartDesc(User owner, LocalDateTime localDateTime, Pageable pageable);

    List<Booking> findBookingsByItemOwnerAndEndBeforeOrderByStartDesc(User owner, LocalDateTime localDateTime);

    Page<Booking> findBookingsByItemOwnerAndEndBeforeOrderByStartDesc(User owner, LocalDateTime localDateTime, Pageable pageable);

    List<Booking> findBookingsByItemOwnerIsAndStatusIsOrderByStartDesc(User owner, Booking.BookingState bookingState);

    Page<Booking> findBookingsByItemOwnerIsAndStatusIsOrderByStartDesc(User owner, Booking.BookingState bookingState, Pageable pageable);

    List<Booking> findBookingsByItemOwnerIsAndStartBeforeAndEndAfterOrderByStartDesc(User owner, LocalDateTime startDateTime, LocalDateTime endDateTime);

    Page<Booking> findBookingsByItemOwnerIsAndStartBeforeAndEndAfterOrderByStartDesc(User owner, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);

    List<Booking> findBookingsByItem_IdAndItem_Owner_IdIsOrderByStart(Integer itemId, Integer userId);


    //endregion
}
