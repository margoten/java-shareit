package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.utils.DateFormatUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestDto() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                1,
                "Some text",
                2,
                LocalDateTime.now(),
                List.of());

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemRequestDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemRequestDto.getDescription());
        assertThat(result).extractingJsonPathNumberValue("$.requestorId").isEqualTo(itemRequestDto.getRequestorId());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(DateFormatUtils.formatDateTime(itemRequestDto.getCreated()));
        assertThat(result).extractingJsonPathArrayValue("$.items").isNullOrEmpty();
    }
}