package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.exception.AuthorizationExc;
import ru.practicum.shareit.exception.EntityNotFoundExc;
import ru.practicum.shareit.exception.MainPropDuplicateExc;
import ru.practicum.shareit.exception.ValidationExc;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserJpaRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class BookingServiceLogicTests {
    private final BookingJpaRepository mockBookingJpaRepository = Mockito.mock(BookingJpaRepository.class);
    private final BookingMapper bookingMapper = Mockito.mock(BookingMapper.class);
    private final UserJpaRepository mockUserJpaRepository = Mockito.mock(UserJpaRepository.class);
    private final BookingService bookingService = new BookingService(mockBookingJpaRepository, bookingMapper, mockUserJpaRepository);

    private final Item item1 = Item.builder()
            .itemId(1L)
            .itemName("Item1")
            .itemDescription("Item1 desc")
            .isItemAvailable(true)
            .owner(User.builder().userId(1L).build())
            .build();

    private final Booking booking1 = Booking.builder()
            .bookingId(1L)
            .bookingStart(LocalDateTime.now().plusDays(1))
            .bookingEnd(LocalDateTime.now().plusDays(2))
            .bookingStatus(BookingStatus.WAITING)
            .booker(User.builder().userId(2L).build())
            .userIdHeader(2L)
            .build();



    @Test
    void bookingServiceLogicTests() {
        when(mockUserJpaRepository.existsById(anyLong())).thenReturn(true);

        assertThrows(EntityNotFoundExc.class, () -> bookingService.validateBookingService(booking1, false, ""),
                "Если item null не брошено исключение EntityNotFoundExc");

        booking1.setBookingItem(item1);
        item1.setIsItemAvailable(false);
        assertThrows(ValidationExc.class, () -> bookingService.validateBookingService(booking1, false, ""),
                "Если вещь не доступна для бронирования не брошено исключение ValidationExc");

        item1.setIsItemAvailable(true);

        item1.setOwner(booking1.getBooker());
        assertThrows(EntityNotFoundExc.class, () -> bookingService.validateBookingService(booking1, false, ""),
                "При попытке бронирования владельцем своей вещи не брошено исключение EntityNotFoundExc");
        item1.setOwner(User.builder().userId(1L).build());

        booking1.setUserIdHeader(-1L);
        when(mockUserJpaRepository.existsById(-1L)).thenReturn(false);
        assertThrows(EntityNotFoundExc.class, () -> bookingService.validateBookingService(booking1, false, ""),
                "Если userIdHeader не найден не брошено исключение EntityNotFoundExc");

        booking1.setUserIdHeader(2L);
        when(mockUserJpaRepository.existsById(2L)).thenReturn(true);
        when(mockBookingJpaRepository.getReferenceById(any())).thenReturn(booking1);
        assertThrows(EntityNotFoundExc.class, () -> bookingService.validateBookingService(booking1, true, ""),
                "При попытке обновлении бронирования владельцем бронирования не брошено исключение EntityNotFoundExc");

        booking1.setUserIdHeader(3L);
        when(mockUserJpaRepository.existsById(3L)).thenReturn(true);
        when(mockBookingJpaRepository.getReferenceById(any())).thenReturn(booking1);
        assertThrows(AuthorizationExc.class, () -> bookingService.validateBookingService(booking1, true, ""),
                "При попытке обновлении бронирования не владельцем вещи не брошено исключение AuthorizationExc");

        booking1.setBookingStart(null);
        assertThrows(ValidationExc.class, () -> bookingService.validateBookingService(booking1, false, ""),
                "Если при создании вещи даты бронирования null, не брошено исключение ValidationExc");

        booking1.setBookingStart(LocalDateTime.now().minusDays(1));
        assertThrows(ValidationExc.class, () -> bookingService.validateBookingService(booking1, false, ""),
                "Если при создании вещи дата начала бронирования в прошлом, не брошено исключение ValidationExc");

        booking1.setBookingStart(LocalDateTime.now().plusDays(1));
        booking1.setBookingEnd(LocalDateTime.now().plusHours(23));
        assertThrows(ValidationExc.class, () -> bookingService.validateBookingService(booking1, false, ""),
                "Если при создании вещи дата конца бронирования раньше чем начала, не брошено исключение ValidationExc");
        booking1.setBookingStart(LocalDateTime.now().plusDays(1));
        booking1.setBookingEnd(LocalDateTime.now().plusDays(2));

        booking1.setUserIdHeader(null);
        assertThrows(MainPropDuplicateExc.class, () -> bookingService.validateBookingService(booking1, false, ""),
                "При отсутствии заголовка UserIdHeader, не брошено исключение MainPropDuplicateExc");

        assertThrows(EntityNotFoundExc.class, () -> bookingService.getEntityService(booking1.getBookingId(), -1L),
                "При запросе бронирования с неизвестным UserIdHeader не брошено исключение EntityNotFoundExc");

        booking1.setUserIdHeader(3L);
        when(mockBookingJpaRepository.existsById(1L)).thenReturn(true);
        bookingService.getEntityService(booking1.getBookingId(), 1L);
        Mockito.verify(mockBookingJpaRepository, Mockito.times(3)).getReferenceById(booking1.getBookingId());
        bookingService.getEntityService(booking1.getBookingId(), 2L);
        Mockito.verify(mockBookingJpaRepository, Mockito.times(5)).getReferenceById(booking1.getBookingId());
        assertThrows(EntityNotFoundExc.class, () -> bookingService.getEntityService(booking1.getBookingId(), 3L),
                "При запросе бронирования не владельцем вещи или бронирования не брошено исключение EntityNotFoundExc");

        assertThrows(ValidationExc.class, () -> bookingService.getEntityService(null, 3L, "0", "10", "UnknownState"),
                "При запросе бронирований с неизвестным BookingState не брошено исключение ValidationExc");

        booking1.setBookingStatus(BookingStatus.APPROVED);
        assertThrows(ValidationExc.class, () -> bookingService.updateBookingService(1L, 1L, true),
                "При попытке изменить статус после одобрения не брошено исключение ValidationExc");


        when(mockBookingJpaRepository.existsById(-1L)).thenReturn(false);
        assertThrows(EntityNotFoundExc.class, () -> bookingService.bookingExistCheck(-1L, ""),
                "При поиске несуществующей вещи не брошено исключение EntityNotFoundExc");
    }
}
