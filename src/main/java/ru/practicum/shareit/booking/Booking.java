package ru.practicum.shareit.booking;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "bookings", schema = "public")
@ToString
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "start_date", nullable = false)
//    @DateTimeFormat(pattern = "hh:mm yyyy-MM-dd")
    private LocalDateTime start;

    @Column(name = "end_date", nullable = false)
//    @DateTimeFormat(pattern = "hh:mm yyyy-MM-dd")
    private LocalDateTime end;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne
    @JoinColumn(name = "booker_id", nullable = false)
    private User booker;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BookingState status;

    public enum BookingState {
        WAITING(0),
        APPROVED(1),
        REJECTED(2),
        CANCELED(3);

        private final int id;

        BookingState(int id) {
            this.id = id;
        }
    }
}
