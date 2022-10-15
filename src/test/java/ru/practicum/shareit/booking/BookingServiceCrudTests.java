package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class BookingServiceCrudTests {
    private static final BookingJpaRepository mockBookingJpaRepository = Mockito.mock(BookingJpaRepository.class);
    private static final BookingMapper bookingMapper = Mockito.mock(BookingMapper.class);
    private static final UserJpaRepository mockUserJpaRepository = Mockito.mock(UserJpaRepository.class);
    private final BookingService bookingService = new BookingService(mockBookingJpaRepository, bookingMapper, mockUserJpaRepository);

    private static final User owner1 = User.builder()
            .userId(1L)
            .userName("Owner1")
            .email("Owner1@test.ru")
            .build();

    private static final Item item1 = Item.builder()
            .itemId(1L)
            .itemName("Item1")
            .itemDescription("Item1 desc")
            .isItemAvailable(true)
            .owner(owner1)
            .userIdHeader(1L)
            .build();

    private static final Booking booking1 = Booking.builder()
            .bookingId(1L)
            .bookingStart(LocalDateTime.now().plusDays(1))
            .bookingEnd(LocalDateTime.now().plusDays(2))
            .bookingStatus(BookingStatus.WAITING)
            .booker(User.builder().userId(2L).build())
            .bookingItem(item1)
            .userIdHeader(2L)
            .build();

    BookingDto bookingDto1 = BookingDto.builder()
            .bookingId(1L)
            .bookingStart(booking1.getBookingStart())
            .bookingEnd(booking1.getBookingEnd())
            .bookingStatus(BookingStatus.WAITING)
            .bookingItem(new BookingDto.ItemDtoForBooking(item1))
            .build();

    @BeforeAll
    static void whenThenMockingSetup() {
        when(mockUserJpaRepository.existsById(Mockito.anyLong())).thenReturn(true);
        when(mockBookingJpaRepository.existsById(Mockito.anyLong())).thenReturn(true);
        when(mockBookingJpaRepository.getReferenceById(Mockito.anyLong())).thenReturn(booking1);
        when(bookingMapper.dtoToBooking(any(BookingDto.class), any())).thenReturn(booking1);
    }

    @AfterEach
    void resetMockCount() {
        Mockito.clearInvocations(mockBookingJpaRepository);
    }

    @Test
    void bookingServiceCreateTests() {
        bookingService.createEntityService(bookingDto1, 1L);
        Mockito.verify(mockBookingJpaRepository, Mockito.times(1)).save(any(Booking.class));
    }

    @Test
    void bookingServiceReadTests() {
        //getOneById
        when(mockBookingJpaRepository.getReferenceById(Mockito.anyLong())).thenReturn(booking1);
        bookingService.getEntityService(2L, 1L);
        Mockito.verify(mockBookingJpaRepository, Mockito.times(3)).getReferenceById(2L);

        //getAllByBookerId
        when(mockBookingJpaRepository.findAllByBookerIdAndStatus(any(), any(BookingStatus.class), any())).thenReturn(List.of());
        when(mockBookingJpaRepository.findAllByBookerIdAndStateAll(any(), any())).thenReturn(List.of());
        when(mockBookingJpaRepository.findAllByBookerIdAndStatePast(any(), any(), any())).thenReturn(List.of());
        when(mockBookingJpaRepository.findAllByBookerIdAndStateCurrent(any(), any(), any())).thenReturn(List.of());
        when(mockBookingJpaRepository.findAllByBookerIdAndStateFuture(any(), any(), any())).thenReturn(List.of());

        bookingService.getEntityService(null, 1L, "0", "10", BookingState.ALL.name());
        bookingService.getEntityService(null, 1L, "0", "10", BookingState.CURRENT.name());
        bookingService.getEntityService(null, 1L, "0", "10", BookingState.PAST.name());
        bookingService.getEntityService(null, 1L, "0", "10", BookingState.FUTURE.name());
        bookingService.getEntityService(null, 1L, "0", "10", BookingState.WAITING.name());
        bookingService.getEntityService(null, 1L, "0", "10", BookingState.REJECTED.name());
        Mockito.verify(mockBookingJpaRepository, Mockito.times(2)).findAllByBookerIdAndStatus(any(), any(BookingStatus.class), any());
        Mockito.verify(mockBookingJpaRepository, Mockito.times(1)).findAllByBookerIdAndStateAll(any(), any());
        Mockito.verify(mockBookingJpaRepository, Mockito.times(1)).findAllByBookerIdAndStatePast(any(), any(), any());
        Mockito.verify(mockBookingJpaRepository, Mockito.times(1)).findAllByBookerIdAndStateCurrent(any(), any(), any());
        Mockito.verify(mockBookingJpaRepository, Mockito.times(1)).findAllByBookerIdAndStateFuture(any(), any(), any());

        //getAllByItemsOwnerId
        when(mockBookingJpaRepository.findAllByItemsOwnerIdAndStatus(any(), any(BookingStatus.class), any())).thenReturn(List.of());
        when(mockBookingJpaRepository.findAllByItemsOwnerIdAndStateAll(any(), any())).thenReturn(List.of());
        when(mockBookingJpaRepository.findAllByItemsOwnerIdAndStatePast(any(), any(), any())).thenReturn(List.of());
        when(mockBookingJpaRepository.findAllByItemsOwnerIdAndStateCurrent(any(), any(), any())).thenReturn(List.of());
        when(mockBookingJpaRepository.findAllByItemsOwnerIdAndStateFuture(any(), any(), any())).thenReturn(List.of());

        bookingService.getEntityService(-111222333L, 1L, "0", "10", BookingState.ALL.name());
        bookingService.getEntityService(-111222333L, 1L, "0", "10", BookingState.CURRENT.name());
        bookingService.getEntityService(-111222333L, 1L, "0", "10", BookingState.PAST.name());
        bookingService.getEntityService(-111222333L, 1L, "0", "10", BookingState.FUTURE.name());
        bookingService.getEntityService(-111222333L, 1L, "0", "10", BookingState.WAITING.name());
        bookingService.getEntityService(-111222333L, 1L, "0", "10", BookingState.REJECTED.name());
        Mockito.verify(mockBookingJpaRepository, Mockito.times(2)).findAllByItemsOwnerIdAndStatus(any(), any(BookingStatus.class), any());
        Mockito.verify(mockBookingJpaRepository, Mockito.times(1)).findAllByItemsOwnerIdAndStateAll(any(), any());
        Mockito.verify(mockBookingJpaRepository, Mockito.times(1)).findAllByItemsOwnerIdAndStatePast(any(), any(), any());
        Mockito.verify(mockBookingJpaRepository, Mockito.times(1)).findAllByItemsOwnerIdAndStateCurrent(any(), any(), any());
        Mockito.verify(mockBookingJpaRepository, Mockito.times(1)).findAllByItemsOwnerIdAndStateFuture(any(), any(), any());
    }

    @Test
    void bookingServiceUpdateTests() {
        bookingService.updateBookingService(1L, 1L, false);
        Mockito.verify(mockBookingJpaRepository, Mockito.times(1)).save(any(Booking.class));
        bookingService.updateBookingService(1L, 1L, true);
        Mockito.verify(mockBookingJpaRepository, Mockito.times(2)).save(any(Booking.class));
    }

    @Test
    void bookingServiceUpdatePlugTests() {
        bookingService.updateEntityService(null, null, null);
        verifyNoInteractions(mockBookingJpaRepository);
    }

    @Test
    void bookingServiceDeleteTests() {
        //deleteOneById
        bookingService.deleteEntityService(3L);
        Mockito.verify(mockBookingJpaRepository, Mockito.times(1)).deleteById(3L);

        //deleteAll
        bookingService.deleteEntityService(null);
        Mockito.verify(mockBookingJpaRepository, Mockito.times(1)).deleteAll();
    }
}
