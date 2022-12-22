package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingExtendedDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

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
    private final ItemService itemService;

    @GetMapping()
    public List<BookingExtendedDto> getBookings(@RequestParam(required = false) String state, @RequestHeader(required = false, value = "X-Sharer-User-Id") Integer userId) {
        return bookingService.getBookings(userId, state)
                .stream()
                .map(BookingMapper::toBookingExtendedDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingExtendedDto> getOwnerBookings(@RequestParam(required = false) String state, @RequestHeader(required = false, value = "X-Sharer-User-Id") Integer userId) {
        return bookingService.getOwnersBookings(userId, state)
                .stream()
                .map(BookingMapper::toBookingExtendedDto)
                .collect(Collectors.toList());
    }

    @PostMapping()
    public BookingExtendedDto createBooking(@RequestBody BookingCreateDto bookingDto, @RequestHeader(required = false, value = "X-Sharer-User-Id") Integer userId) {
        Item item = itemService.getItem(bookingDto.getItemId());
        return BookingMapper.toBookingExtendedDto(bookingService.createBooking(BookingMapper.toBooking(bookingDto), item, userId));
    }

    @PatchMapping("/{bookingId}")
    public BookingExtendedDto approveBooking(@PathVariable Integer bookingId, @RequestParam(required = false) boolean approved, @RequestHeader(required = false, value = "X-Sharer-User-Id") Integer userId) {
        return BookingMapper.toBookingExtendedDto(bookingService.approveBooking(bookingId, approved, userId));
    }

    @GetMapping("/{bookingId}")
    public BookingExtendedDto getBooking(@PathVariable Integer bookingId, @RequestHeader(required = false, value = "X-Sharer-User-Id") Integer userId) {
        return BookingMapper.toBookingExtendedDto(bookingService.getBooking(bookingId, userId));
    }

}
