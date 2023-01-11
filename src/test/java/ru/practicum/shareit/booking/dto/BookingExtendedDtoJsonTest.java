package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.utils.DateFormatUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingExtendedDtoJsonTest {
    @Autowired
    private JacksonTester<BookingExtendedDto> json;

    @Test
    void testItemDto() throws Exception {
        BookingExtendedDto bookingExtendedDto = new BookingExtendedDto(1,
                LocalDateTime.of(2023, 1, 1, 0, 1, 1),
                LocalDateTime.of(2023, 1, 1, 1, 1, 1),
                new ItemDto(
                        1,
                        "Item1",
                        "Description1",
                        true, 1, 1),
                new UserDto(
                        1,
                        "Harry",
                        "mail@mail.com"),
                Booking.BookingState.WAITING.name());

        JsonContent<BookingExtendedDto> result = json.write(bookingExtendedDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(bookingExtendedDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(DateFormatUtils.formatDateTime(bookingExtendedDto.getStart()));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(DateFormatUtils.formatDateTime(bookingExtendedDto.getEnd()));
        assertThat(result).extractingJsonPathMapValue("$.item").isNotNull();
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(bookingExtendedDto.getItem().getId());
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo(bookingExtendedDto.getItem().getName());
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo(bookingExtendedDto.getItem().getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(bookingExtendedDto.getItem().getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.item.ownerId").isEqualTo(bookingExtendedDto.getItem().getOwnerId());
        assertThat(result).extractingJsonPathNumberValue("$.item.requestId").isEqualTo(bookingExtendedDto.getItem().getRequestId());
        assertThat(result).extractingJsonPathMapValue("$.booker").isNotNull();
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(bookingExtendedDto.getBooker().getId());
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo(bookingExtendedDto.getBooker().getName());
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo(bookingExtendedDto.getBooker().getEmail());
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(bookingExtendedDto.getStatus());


    }
}