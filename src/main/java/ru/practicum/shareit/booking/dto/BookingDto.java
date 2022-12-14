package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDto {
    private Integer id;
    @DateTimeFormat(pattern = "hh:mm yyyy-MM-dd")
    private LocalDateTime start;
    @DateTimeFormat(pattern = "hh:mm yyyy-MM-dd")
    private LocalDateTime end;
    private Integer item;
    private Integer booker;
    private String status;
}
