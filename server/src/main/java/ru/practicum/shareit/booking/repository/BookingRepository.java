package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    //region Методы запросов бронирования для автора бронирования
    boolean existsBookingByBookerIsAndEndBefore(User booker, LocalDateTime localDateTime);

    List<Booking> findBookingsByItem_IdIsAndStatusIsAndEndIsAfter(Integer itemId, Booking.BookingState bookingState, LocalDateTime localDateTime);

    Page<Booking> findBookingsByBookerIsOrderByStartDesc(User booker, Pageable pageable);

    Page<Booking> findBookingsByBookerIsAndStatusIsOrderByStartDesc(User booker, Booking.BookingState bookingState, Pageable pageable);

    Page<Booking> findBookingsByBookerIsAndStartIsAfterOrderByStartDesc(User booker, LocalDateTime localDateTime, Pageable pageable);

    Page<Booking> findBookingsByBookerIsAndEndBeforeOrderByStartDesc(User booker, LocalDateTime localDateTime, Pageable pageable);

    Page<Booking> findBookingsByBookerIsAndStartBeforeAndEndAfterOrderByStartDesc(User booker, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);
    //endregion

    //region Методы запросов бронирования для владельца товара
    Page<Booking> findBookingsByItemOwnerIsOrderByStartDesc(User owner, Pageable pageable);

    Page<Booking> findBookingsByItemOwnerAndStartAfterOrderByStartDesc(User owner, LocalDateTime localDateTime, Pageable pageable);

    Page<Booking> findBookingsByItemOwnerAndEndBeforeOrderByStartDesc(User owner, LocalDateTime localDateTime, Pageable pageable);

    Page<Booking> findBookingsByItemOwnerIsAndStatusIsOrderByStartDesc(User owner, Booking.BookingState bookingState, Pageable pageable);

    Page<Booking> findBookingsByItemOwnerIsAndStartBeforeAndEndAfterOrderByStartDesc(User owner, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);

    List<Booking> findBookingsByItem_IdAndItem_Owner_IdIsOrderByStart(Integer itemId, Integer userId);

    List<Booking> findBookingsByItemInOrderByStartDesc(List<Item> items);


    //endregion
}
