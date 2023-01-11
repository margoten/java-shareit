package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.utils.DateFormatUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingCreateDtoJsonTest {
    @Autowired
    private JacksonTester<BookingCreateDto> json;

    @Test
    void testItemDto() throws Exception {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(1,
                LocalDateTime.of(2023, 1, 1, 0, 1, 1),
                LocalDateTime.of(2023, 1, 1, 1, 1, 1),
                1,
                1,
                Booking.BookingState.WAITING.name());

        JsonContent<BookingCreateDto> result = json.write(bookingCreateDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(bookingCreateDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(DateFormatUtils.formatDateTime(bookingCreateDto.getStart()));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(DateFormatUtils.formatDateTime(bookingCreateDto.getEnd()));
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(bookingCreateDto.getItemId());
        assertThat(result).extractingJsonPathNumberValue("$.booker").isEqualTo(bookingCreateDto.getBooker());
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(bookingCreateDto.getStatus());
    }

}