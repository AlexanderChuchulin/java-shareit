package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserJpaRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingMappingTests {
    private final UserJpaRepository mockUserJpaRepository = Mockito.mock(UserJpaRepository.class);
    private final ItemJpaRepository mockItemJpaRepository = Mockito.mock(ItemJpaRepository.class);
    private final BookingMapper bookingMapper = new BookingMapper(mockUserJpaRepository, mockItemJpaRepository);

    @Test
    void bookingToDtoTests() {
        Booking booking1 = Booking.builder()
                .bookingId(1L)
                .bookingStart(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS))
                .bookingEnd(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.MILLIS))
                .bookingStatus(BookingStatus.WAITING)
                .booker(User.builder().userId(2L).build())
                .bookingItem(Item.builder().itemId(1L).build())
                .userIdHeader(1L)
                .build();

        BookingDto bookingDtoTest1 = bookingMapper.bookingToDto(booking1);

        assertEquals(booking1.getBookingId(), bookingDtoTest1.getBookingId(), "Booking->BookingDto не совпадает id");
        assertEquals(booking1.getBookingStart(), bookingDtoTest1.getBookingStart(), "Booking->BookingDto не совпадает Start");
        assertEquals(booking1.getBookingEnd(), bookingDtoTest1.getBookingEnd(), "Booking->BookingDto не совпадает End");
        assertEquals(booking1.getBookingStatus(), bookingDtoTest1.getBookingStatus(), "Booking->BookingDto не совпадает Status");
    }

    @Test
    void dtoToBookingTests() {
        BookingDto bookingDto1 = BookingDto.builder()
                .bookingId(1L)
                .bookingStart(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS))
                .bookingEnd(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.MILLIS))
                .bookingStatus(BookingStatus.WAITING)
                .bookingItem(new BookingDto.ItemDtoForBooking(new Item()))
                .userIdHeader(1L)
                .itemId(1L)
                .build();

        Booking bookingTest1 = bookingMapper.dtoToBooking(bookingDto1, 1L);

        assertEquals(bookingTest1.getBookingId(), bookingDto1.getBookingId(), "Booking->BookingDto не совпадает id");
        assertEquals(bookingTest1.getBookingEnd(), bookingDto1.getBookingEnd(), "Booking->BookingDto не совпадает End");
        assertEquals(bookingTest1.getBookingStatus(), bookingDto1.getBookingStatus(), "Booking->BookingDto не совпадает Status");
        assertEquals(bookingTest1.getUserIdHeader(), bookingDto1.getUserIdHeader(), "Booking->BookingDto не совпадает UserIdHeader");
    }
}
