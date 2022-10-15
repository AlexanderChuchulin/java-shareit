package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class BookingIntegrationTests {
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private ItemJpaRepository itemJpaRepository;
    @Autowired
    private BookingJpaRepository bookingJpaRepository;
    @Autowired
    private BookingService bookingService;

    @Test
    void bookingIntegrationTests() {
        User user1 = User.builder()
                .userId(1L)
                .userName("User1")
                .email("User1@test.ru")
                .userIdHeader(1L)
                .build();
        userJpaRepository.save(user1);

        User user2 = User.builder()
                .userId(2L)
                .userName("User2")
                .email("User2@test.ru")
                .userIdHeader(2L)
                .build();
        userJpaRepository.save(user2);


        User user3 = User.builder()
                .userId(3L)
                .userName("User3")
                .email("User3@test.ru")
                .userIdHeader(3L)
                .build();
        userJpaRepository.save(user3);

        Item item1 = Item.builder()
                .itemId(1L)
                .itemName("Item1")
                .itemDescription("Item1 desc")
                .isItemAvailable(true)
                .owner(user1)
                .userIdHeader(1L)
                .build();

        itemJpaRepository.save(item1);

        BookingDto bookingDto1 = BookingDto.builder()
                .bookingId(1L)
                .bookingStart(LocalDateTime.now().plusDays(1))
                .bookingEnd(LocalDateTime.now().plusDays(2))
                .bookingStatus(BookingStatus.WAITING)
                .bookingItem(new BookingDto.ItemDtoForBooking(new Item()))
                .userIdHeader(2L)
                .itemId(1L)
                .build();

        BookingDto bookingDto2 = BookingDto.builder()
                .bookingId(2L)
                .bookingStart(LocalDateTime.now().plusDays(1))
                .bookingEnd(LocalDateTime.now().plusDays(2))
                .bookingStatus(BookingStatus.WAITING)
                .bookingItem(new BookingDto.ItemDtoForBooking(new Item()))
                .userIdHeader(2L)
                .itemId(1L)
                .build();

        BookingDto bookingDto3 = BookingDto.builder()
                .bookingId(3L)
                .bookingStart(LocalDateTime.now().plusDays(1))
                .bookingEnd(LocalDateTime.now().plusDays(2))
                .bookingStatus(BookingStatus.WAITING)
                .bookingItem(new BookingDto.ItemDtoForBooking(new Item()))
                .userIdHeader(3L)
                .itemId(1L)
                .build();

        // *Create
        assertEquals(0, bookingJpaRepository.count(), "Перед тестами в БД брони есть записи");
        BookingDto bookingDtoTest1 = bookingService.createEntityService(bookingDto1, 2L);
        assertNotNull(bookingDtoTest1.getBookingId(), "После создания вещи в БД ей не присвоен id");
        assertEquals(1, bookingJpaRepository.count(), "После создания брони в БД не 1 запись");
        BookingDto bookingDtoTest2 = bookingService.createEntityService(bookingDto2, 2L);
        assertEquals(2, bookingJpaRepository.count(), "После создания брони в БД не 2 записи");
        BookingDto bookingDtoTest3 = bookingService.createEntityService(bookingDto3, 3L);


        // *Read
        //getOneById
        assertEquals(bookingDtoTest2, bookingService.getEntityService(2L, 1L),
                "Получить бронь по id. Брони не совпадают");

        //getAllByBookerId
        assertEquals(List.of(bookingDtoTest1, bookingDtoTest2), bookingService
                        .getEntityService(null, 2L, "0", "10", BookingState.ALL.name()),
                "Получить все брони по id null по userIdHeader владельца брони, списки не совпадают");

        //getAllByItemsOwnerId
        assertEquals(List.of(bookingDtoTest1, bookingDtoTest2, bookingDtoTest3), bookingService
                        .getEntityService(-111222333L, 1L, "0", "10", BookingState.ALL.name()),
                "Получить все брони по id -111222333L по userIdHeader владельца вещи, списки не совпадают");

        // *JPA
        assertEquals(List.of(), itemJpaRepository.findAllByRequestRequestId(-1L),
                "Получить все вещи по несуществующему id запроса, возвращает не пустой список");
    }
}
