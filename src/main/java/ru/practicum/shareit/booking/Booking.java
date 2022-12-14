package ru.practicum.shareit.booking;

import lombok.Data;
import lombok.NoArgsConstructor;
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
@Entity
@Table(name = "booking", schema = "public")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "start", nullable = false)
    private LocalDateTime start;
    @Column(name = "end", nullable = false)
    private LocalDateTime end;
    @ManyToOne
    @JoinColumn(name = "id", nullable = false)
    private Item item;
    @ManyToOne
    @JoinColumn(name = "id", nullable = false)
    private User booker;
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingState status;

    public Booking(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

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
