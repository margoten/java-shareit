package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingExtendedDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.PaginationUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private static final Pageable PAGEABLE_DEFAULT = PaginationUtils.createPageRequest(0, 100, Sort.by("start").descending());

    @Override
    public BookingExtendedDto createBooking(BookingCreateDto bookingCreateDto, ItemExtendedDto itemDto, Integer bookerId) {
        if (!itemDto.getAvailable()) {
            throw new ValidationException("Товар с id " + itemDto.getId() + " не доступен до бронирования");
        }

        if (itemDto.getOwnerId().equals(bookerId)) {
            throw new NotFoundException("Товар с id " + itemDto.getId() + " не может быть заброванирован владельцем");
        }
        User booker = UserMapper.toUser(userService.getUser(bookerId));
        Item item = ItemMapper.toItem(itemDto);
        User owner = UserMapper.toUser(userService.getUser(itemDto.getOwnerId()));
        item.setOwner(owner);

        validationBooking(bookingCreateDto);

        List<Booking> bookings = bookingRepository.findBookingsByItem_IdIsAndStatusIsAndEndIsAfter(item.getId(), Booking.BookingState.APPROVED, bookingCreateDto.getStart());
        if (!bookings.isEmpty()) {
            throw new NotFoundException("Недоступный товар для бронирования " + item.getName());
        }
        Booking booking = BookingMapper.toBooking(bookingCreateDto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Booking.BookingState.WAITING);

        return BookingMapper.toBookingExtendedDto(bookingRepository.save(booking));
    }

    @Override
    public BookingExtendedDto approveBooking(Integer bookingId, boolean approved, Integer userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Бронирования с id = " + bookingId + " не существует."));
        if (booking.getBooker().getId().equals(userId)) {
            throw new NotFoundException("Не существует доступного для обновления бронирования для пользователя с id = " + userId + ".");

        }
        if (!booking.getItem().getOwner().getId().equals(userId) || !booking.getStatus().equals(Booking.BookingState.WAITING)) {
            throw new ValidationException("Статус бронирования не может быть обновлен");
        }
        booking.setStatus(approved ? Booking.BookingState.APPROVED : Booking.BookingState.REJECTED);
        return BookingMapper.toBookingExtendedDto(bookingRepository.save(booking));
    }

    @Override
    public BookingExtendedDto getBooking(Integer id, Integer userId) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Бронирования с id = " + id + " не существует."));
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Не существует доступного бронирования для пользователя с id = " + userId + ".");
        }
        return BookingMapper.toBookingExtendedDto(booking);
    }

    @Override
    public List<BookingExtendedDto> getBookings(Integer bookerId, String state, Integer from, Integer size) {
        Pageable pageable = from == null || size == null
                ? PAGEABLE_DEFAULT
                : PaginationUtils.createPageRequest(from, size, Sort.by("start").descending());

        return getBookingsStream(bookerId, state, pageable)
                .map(BookingMapper::toBookingExtendedDto)
                .collect(Collectors.toList());

    }

    private Stream<Booking> getBookingsStream(Integer bookerId, String state, Pageable pageable) {
        User booker = UserMapper.toUser(userService.getUser(bookerId));
        if (state == null || state.equals(Booking.TimeBookingState.ALL.name())) {
            return bookingRepository.findBookingsByBookerIsOrderByStartDesc(booker, pageable).stream();
        }
        if (state.equals(Booking.TimeBookingState.FUTURE.name())) {
            return bookingRepository.findBookingsByBookerIsAndStartIsAfterOrderByStartDesc(booker, LocalDateTime.now(), pageable).stream();
        }
        if (state.equals(Booking.TimeBookingState.CURRENT.name())) {
            return bookingRepository.findBookingsByBookerIsAndStartBeforeAndEndAfterOrderByStartDesc(booker, LocalDateTime.now(), LocalDateTime.now(), null).stream();
        }

        if (state.equals(Booking.TimeBookingState.PAST.name())) {
            return bookingRepository.findBookingsByBookerIsAndEndBeforeOrderByStartDesc(booker, LocalDateTime.now(), pageable).stream();
        }
        if (Arrays.stream(Booking.BookingState.values()).anyMatch(bookingState -> bookingState.name().equals(state))) {
            return bookingRepository.findBookingsByBookerIsAndStatusIsOrderByStartDesc(booker, Booking.BookingState.valueOf(state), pageable).stream();
        }
        throw new ValidationException("Unknown state: " + state);
    }

    @Override
    public List<BookingExtendedDto> getOwnersBookings(Integer userId, String state, Integer from, Integer size) {
        Pageable pageable = from == null || size == null
                ? PAGEABLE_DEFAULT
                : PaginationUtils.createPageRequest(from, size, Sort.by("start").descending());

        return getOwnersBookingsStream(userId, state, pageable)
                .map(BookingMapper::toBookingExtendedDto)
                .collect(Collectors.toList());


    }

    private Stream<Booking> getOwnersBookingsStream(Integer userId, String state, @PageableDefault(sort = {"start",}, value = 100, direction = Sort.Direction.DESC) Pageable pageable) {
        User owner = UserMapper.toUser(userService.getUser(userId));
        if (state == null || state.equals(Booking.TimeBookingState.ALL.name())) {
            return bookingRepository.findBookingsByItemOwnerIsOrderByStartDesc(owner, pageable).stream();
        }
        if (state.equals(Booking.TimeBookingState.FUTURE.name())) {
            return bookingRepository.findBookingsByItemOwnerAndStartAfterOrderByStartDesc(owner, LocalDateTime.now(), pageable).stream();
        }
        if (state.equals(Booking.TimeBookingState.CURRENT.name())) {
            return bookingRepository.findBookingsByItemOwnerIsAndStartBeforeAndEndAfterOrderByStartDesc(owner, LocalDateTime.now(), LocalDateTime.now(), pageable).stream();
        }

        if (state.equals(Booking.TimeBookingState.PAST.name())) {
            return bookingRepository.findBookingsByItemOwnerAndEndBeforeOrderByStartDesc(owner, LocalDateTime.now(), pageable).stream();
        }
        if (Arrays.stream(Booking.BookingState.values()).anyMatch(bookingState -> bookingState.name().equals(state))) {
            return bookingRepository.findBookingsByItemOwnerIsAndStatusIsOrderByStartDesc(owner, Booking.BookingState.valueOf(state), pageable).stream();
        }
        throw new ValidationException("Unknown state: " + state);
    }

    @Override
    public List<BookingExtendedDto> getBookingsByItem(Integer itemId, Integer userId) {
        return bookingRepository.findBookingsByItem_IdAndItem_Owner_IdIsOrderByStart(itemId, userId)
                .stream()
                .map(BookingMapper::toBookingExtendedDto)
                .collect(Collectors.toList());
    }

    private void validationBooking(BookingCreateDto booking) {

        if (booking.getStart() == null) {
            throw new ValidationException("Заполните дату начала бронирования.");
        }
        if (booking.getEnd() == null) {
            throw new ValidationException("Заполните дату окончания бронирования.");
        }

        if (booking.getStart().toLocalDate().isBefore(LocalDate.now())) {
            throw new ValidationException("Некорректная дата начала бронирования.");
        }
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getEnd().toLocalDate().isBefore(LocalDate.now())) {
            throw new ValidationException("Некорректная дата окончания бронирования.");
        }
    }
}
