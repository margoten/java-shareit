package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingExtendedDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingController {
    private final BookingService bookingService;


    @GetMapping()
    public List<BookingExtendedDto> getBookings(@RequestParam() String state,
                                                @RequestParam() Integer from,
                                                @RequestParam() Integer size,
                                                @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        return bookingService.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingExtendedDto> getOwnerBookings(@RequestParam() String state,
                                                     @RequestParam() Integer from,
                                                     @RequestParam() Integer size,
                                                     @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        return bookingService.getOwnersBookings(userId, state, from, size);
    }

    @PostMapping()
    public BookingExtendedDto createBooking(@RequestBody BookingCreateDto bookingDto,
                                            @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingExtendedDto approveBooking(@PathVariable Integer bookingId,
                                             @RequestParam(required = false) boolean approved,
                                             @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        return bookingService.approveBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingExtendedDto getBooking(@PathVariable Integer bookingId,
                                         @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        return bookingService.getBooking(bookingId, userId);
    }

}
