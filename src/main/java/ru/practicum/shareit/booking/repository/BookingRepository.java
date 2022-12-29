package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    @Query("select b from Booking b inner join b.item i where i.owner = ?1 order by b.start desc")
    List<Booking> findBookingsByOwnerOrderByStartDesc(User owner);

    @Query("select b from Booking b inner join b.item i where i.owner = ?1 and b.start > ?2 order by b.start desc")
    List<Booking> findBookingsByOwnerAndStartAfterOrderByStartDesc(User owner, LocalDateTime localDateTime);

    @Query("select b from Booking b inner join b.item i where i.owner = ?1 and b.end < ?2 order by b.start desc")
    List<Booking> findBookingsByOwnerAndEndBeforeOrderByStartDesc(User owner, LocalDateTime localDateTime);

    @Query("select b from Booking b inner join b.item i where i.owner = ?1 and b.status = ?2 order by b.start desc")
    List<Booking> findBookingsByOwnerIsAndStatusIsOrderByStartDesc(User owner, Booking.BookingState bookingState);

    @Query("select b from Booking b inner join b.item i where i.owner = ?1 and b.start < ?2 and b.end > ?3 order by b.start desc")
    List<Booking> findBookingsByOwnerIsAndStartBeforeAndEndAfterOrderByStartDesc(User owner, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<Booking> findBookingsByItem_IdOrderByStart(Integer itemId);

    //endregion
}
