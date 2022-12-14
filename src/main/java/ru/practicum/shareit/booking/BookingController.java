package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
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

    @GetMapping()
    public List<BookingDto> getAllItems(@RequestHeader(required = false, value = "X-Sharer-User-Id") Integer userId) {
        return bookingService.getBookings(userId)
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

//    @GetMapping("/search")
//    public List<BookingDto> searchItems(@RequestParam(required = false) String text, @RequestHeader(required = false, value = "X-Sharer-User-Id") Integer userId) {
//        return itemService.searchItems(text, userId)
//                .stream()
//                .map(BookingMapper::toBookingDto)
//                .collect(Collectors.toList());
//    }

    @PostMapping()
    public BookingDto createBooking(@RequestBody BookingDto bookingDto, @RequestHeader(required = false, value = "X-Sharer-User-Id") Integer userId) {
        return BookingMapper.toBookingDto(bookingService.createBooking(BookingMapper.toBooking(bookingDto), userId));
    }

    @PatchMapping("/{itemId}")
    public BookingDto updateBooking(@RequestBody BookingDto bookingDto, @PathVariable int itemId, @RequestHeader(required = false, value = "X-Sharer-User-Id") Integer userId) {
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setId(itemId);
        return BookingMapper.toBookingDto(bookingService.updateBooking(booking, userId));
    }

    @GetMapping("/{itemId}")
    public BookingDto getItem(@PathVariable int itemId) {
        return BookingMapper.toBookingDto(bookingService.getBooking(itemId));
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable int itemId) {
        bookingService.deleteBooking(itemId);
    }
}
