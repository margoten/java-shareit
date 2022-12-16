package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingController {
    private final BookingService bookingService;

    @GetMapping()
    public List<BookingDto> getBookings(@RequestParam(required = false) String state, @RequestHeader(required = false, value = "X-Sharer-User-Id") Integer userId) {
        return bookingService.getBookings(userId, state)
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestParam(required = false) String state, @RequestHeader(required = false, value = "X-Sharer-User-Id") Integer userId) {
        return bookingService.getOwnerBookings(userId, state)
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @PostMapping()
    public BookingDto createBooking(@RequestBody BookingCreateDto bookingDto, @RequestHeader(required = false, value = "X-Sharer-User-Id") Integer userId) {
        return BookingMapper.toBookingDto(bookingService.createBooking(BookingMapper.toBooking(bookingDto), bookingDto.getItemId(), userId));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable Integer bookingId, @RequestParam(required = false) boolean approved, @RequestHeader(required = false, value = "X-Sharer-User-Id") Integer userId) {
        return BookingMapper.toBookingDto(bookingService.approveBooking(bookingId, approved, userId));
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Integer bookingId, @RequestHeader(required = false, value = "X-Sharer-User-Id") Integer userId) {
        return BookingMapper.toBookingDto(bookingService.getBooking(bookingId, userId));
    }

}
