package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemExtendedDtoJsonTest {
    @Autowired
    private JacksonTester<ItemExtendedDto> json;

    @Test
    void testItemDto() throws Exception {
        ItemExtendedDto itemExtendedDto = new ItemExtendedDto(
                1,
                "Item",
                "Description",
                true, 1, null, new BookingDto(1, 1), null, List.of());

        JsonContent<ItemExtendedDto> result = json.write(itemExtendedDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemExtendedDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemExtendedDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemExtendedDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemExtendedDto.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(itemExtendedDto.getOwnerId());
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isNull();
        assertThat(result).extractingJsonPathMapValue("$.lastBooking").isNotNull();
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(itemExtendedDto.getLastBooking().getId());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(itemExtendedDto.getLastBooking().getBookerId());
        assertThat(result).extractingJsonPathValue("$.nextBooking").isNull();
        assertThat(result).extractingJsonPathArrayValue("$.comments").isNullOrEmpty();
    }
}