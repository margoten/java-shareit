package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingExtendedDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;
    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mvc;


    private final BookingExtendedDto bookingExtendedDto = new BookingExtendedDto(1,
            LocalDateTime.of(2023, 1,1,0,1,1),
            LocalDateTime.of(2023, 1,1,1,1,1),
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

    private final BookingCreateDto bookingCreateDto = new BookingCreateDto(1,
            LocalDateTime.of(2023, 1,1,0,1,1),
            LocalDateTime.of(2023, 1,1,1,1,1),
            1,
            1,
            Booking.BookingState.WAITING.name());

    @Test
    void getBookings() throws Exception {
        when(bookingService.getBookings(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingExtendedDto));

        mvc.perform(get("/bookings")
                .header("X-Sharer-User-Id", 1)
                .param("state", "All")
                .param("from", "0")
                .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingExtendedDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].status", is(bookingExtendedDto.getStatus())))
                .andExpect(jsonPath("$[0].start", is(bookingExtendedDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingExtendedDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].item", notNullValue()))
                .andExpect(jsonPath("$[0].booker", notNullValue()));
    }

    @Test
    void getOwnerBookings() throws Exception {
        when(bookingService.getOwnersBookings(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingExtendedDto));

        mvc.perform(get("/bookings/owner")
                .header("X-Sharer-User-Id", 1)
                .param("state", "All")
                .param("from", "0")
                .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingExtendedDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].status", is(bookingExtendedDto.getStatus())))
                .andExpect(jsonPath("$[0].start", is(bookingExtendedDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingExtendedDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].item", notNullValue()))
                .andExpect(jsonPath("$[0].booker", notNullValue()));
    }

    @Test
    void createBooking() throws Exception{
        when(bookingService.createBooking(any(), any(), anyInt()))
                .thenReturn(bookingExtendedDto);

        mvc.perform(post("/bookings")
                .header("X-Sharer-User-Id", 1)
                .content(mapper.writeValueAsString(bookingCreateDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingExtendedDto.getId()), Integer.class))
                .andExpect(jsonPath("$.status", is(bookingExtendedDto.getStatus())))
                .andExpect(jsonPath("$.start", is(bookingExtendedDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingExtendedDto.getEnd().toString())))
                .andExpect(jsonPath("$.item", notNullValue()))
                .andExpect(jsonPath("$.booker", notNullValue()));
    }

    @Test
    void approveBooking() throws Exception {
        when(bookingService.approveBooking(anyInt(), anyBoolean(), anyInt()))
                .thenReturn(bookingExtendedDto);


        mvc.perform(patch("/bookings/{bookingId}", 1)
                .header("X-Sharer-User-Id", 1)
                .param("approved", "true")
                .content(mapper.writeValueAsString(bookingExtendedDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingExtendedDto.getId()), Integer.class))
                .andExpect(jsonPath("$.status", is(bookingExtendedDto.getStatus())))
                .andExpect(jsonPath("$.start", is(bookingExtendedDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingExtendedDto.getEnd().toString())))
                .andExpect(jsonPath("$.item", notNullValue()))
                .andExpect(jsonPath("$.booker", notNullValue()));
    }

    @Test
    void getBooking() throws Exception{
        when(bookingService.getBooking(anyInt(), anyInt()))
                .thenReturn(bookingExtendedDto);

        mvc.perform(get("/bookings/{bookingId}", 1)
                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingExtendedDto.getId()), Integer.class))
                .andExpect(jsonPath("$.status", is(bookingExtendedDto.getStatus())))
                .andExpect(jsonPath("$.start", is(bookingExtendedDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingExtendedDto.getEnd().toString())))
                .andExpect(jsonPath("$.item", notNullValue()))
                .andExpect(jsonPath("$.booker", notNullValue()));
    }
}