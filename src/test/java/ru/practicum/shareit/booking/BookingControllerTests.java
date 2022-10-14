package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTests {
    @MockBean
    private BookingService mockBookingService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    BookingDto bookingDto1 = BookingDto.builder()
            .bookingId(1L)
            .bookingStart(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS))
            .bookingEnd(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS))
            .bookingStatus(BookingStatus.WAITING)
            .userIdHeader(2L)
            .itemId(1L)
            .build();

    BookingDto bookingDto2 = BookingDto.builder()
            .bookingId(2L)
            .bookingStart(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS))
            .bookingEnd(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS))
            .bookingStatus(BookingStatus.WAITING)
            .userIdHeader(2L)
            .itemId(1L)
            .build();

    @Test
    @SneakyThrows
    void createBookingControllerTests() {
        when(mockBookingService.createEntityService(any(), any()))
                .thenReturn(bookingDto1);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(bookingDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto1.getBookingId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto1.getBookingStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto1.getBookingEnd().toString())))
                .andExpect(jsonPath("$.status", is(bookingDto1.getBookingStatus().toString())));

    }

    @Test
    @SneakyThrows
    void getBookingByIdControllerTests() {
        when(mockBookingService.getEntityService(any(), any(), any()))
                .thenReturn(bookingDto2);

        mockMvc.perform(get("/bookings/2")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto2.getBookingId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto2.getBookingStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto2.getBookingEnd().toString())))
                .andExpect(jsonPath("$.status", is(bookingDto2.getBookingStatus().toString())));
    }

    @Test
    @SneakyThrows
    void getBookingsControllerTests() {
        when(mockBookingService.getEntityService(any(), any(), any()))
                .thenReturn(List.of(bookingDto2, bookingDto1));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto2.getBookingId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto2.getBookingStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingDto2.getBookingEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(bookingDto2.getBookingStatus().toString())))
                .andExpect(jsonPath("$[1].id", is(bookingDto1.getBookingId()), Long.class))
                .andExpect(jsonPath("$[1].start", is(bookingDto1.getBookingStart().toString())))
                .andExpect(jsonPath("$[1].end", is(bookingDto1.getBookingEnd().toString())))
                .andExpect(jsonPath("$[1].status", is(bookingDto1.getBookingStatus().toString())));
    }

    @Test
    @SneakyThrows
    void getBookingsByItemOwnerControllerTests() {
        when(mockBookingService.getEntityService(any(), any(), any()))
                .thenReturn(List.of(bookingDto2));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto2.getBookingId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto2.getBookingStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingDto2.getBookingEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(bookingDto2.getBookingStatus().toString())));
    }

    @Test
    @SneakyThrows
    void updateBookingControllerTests() {
        when(mockBookingService.updateBookingService(any(), any(), any()))
                .thenReturn(bookingDto1);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(bookingDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto1.getBookingId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto1.getBookingStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto1.getBookingEnd().toString())))
                .andExpect(jsonPath("$.status", is(bookingDto1.getBookingStatus().toString())));
    }

    @Test
    @SneakyThrows
    void deleteBookingByIdControllerTests() {
        mockMvc.perform(delete("/bookings/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(mockBookingService, Mockito.times(1)).deleteEntityService(1L);
    }

    @Test
    @SneakyThrows
    void deleteAllBookingControllerTests() {
        mockMvc.perform(delete("/bookings/")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(mockBookingService, Mockito.times(1)).deleteEntityService(null);
    }
}
