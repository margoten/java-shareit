package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookItemRequestDto {
    private Long itemId;
    @FutureOrPresent(message = "Incorrect the bookings start time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-DD'T'HH:mm:ss'Z'")
    private LocalDateTime start;
    @Future(message = "Incorrect the bookings end time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-DD'T'HH:mm:ss'Z'")
    private LocalDateTime end;
}
