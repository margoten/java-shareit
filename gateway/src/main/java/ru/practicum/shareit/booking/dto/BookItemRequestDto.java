package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookItemRequestDto {
	private Long itemId;
	@FutureOrPresent(message = "Incorrect the bookings start time")
	private LocalDateTime start;
	@Future(message = "Incorrect the bookings end time")
	private LocalDateTime end;
}
