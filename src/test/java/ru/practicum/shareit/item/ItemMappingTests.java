package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestJpaRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserJpaRepository;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

public class ItemMappingTests {
    private final Booking mockBooking = Mockito.mock(Booking.class);
    private final UserJpaRepository mockUserJpaRepository = Mockito.mock(UserJpaRepository.class);
    private final CommentJpaRepository mockCommentJpaRepository = Mockito.mock(CommentJpaRepository.class);
    private final RequestJpaRepository mockRequestJpaRepository = Mockito.mock(RequestJpaRepository.class);
    private final ItemMapper itemMapper = new ItemMapper(mockUserJpaRepository, mockCommentJpaRepository, mockRequestJpaRepository);

    @Test
    void itemToDtoTests() {
        Item item1 = Item.builder()
                .itemId(1L)
                .itemName("Item1")
                .itemDescription("Item1 desc")
                .isItemAvailable(true)
                .owner(new User())
                .request(new Request())
                .bookingsSet(new HashSet<>())
                .commentsSet(new HashSet<>())
                .userIdHeader(1L)
                .build();

        ItemDto itemDtoTest1 = itemMapper.itemToDto(item1, false);

        assertEquals(item1.getItemId(), itemDtoTest1.getItemId(), "Item->ItemDto не совпадает id");
        assertEquals(item1.getItemName(), itemDtoTest1.getItemName(), "Item->ItemDto не совпадает name");
        assertEquals(item1.getItemDescription(), itemDtoTest1.getItemDescription(), "Item->ItemDto не совпадает description");
        assertEquals(item1.getIsItemAvailable(), itemDtoTest1.getIsItemAvailable(), "Item->ItemDto не совпадает доступность");
    }

    @Test
    void dtoToItemTests() {
        when(mockBooking.getBooker()).thenReturn(new User());

        ItemDto itemDto1 = ItemDto.builder()
                .itemId(1L)
                .itemName("Item1Dto1")
                .itemDescription("Item1Dto1 description")
                .isItemAvailable(true)
                .nextBooking(new ItemDto.BookingDtoForItem(mockBooking))
                .lastBooking(new ItemDto.BookingDtoForItem(mockBooking))
                .commentsDtoForItemList(new ArrayList<>())
                .requestId(1L)
                .userIdHeader(1L)
                .build();

        Item itemTest1 = itemMapper.dtoToItem(itemDto1, 1L);

        assertNull(itemTest1.getItemId(), "ItemDto->Item id не null");
        assertEquals(itemDto1.getItemName(), itemTest1.getItemName(), "ItemDto->Item не совпадает name");
        assertEquals(itemDto1.getItemDescription(), itemTest1.getItemDescription(), "ItemDto->Item не совпадает description");
        assertEquals(itemDto1.getIsItemAvailable(), itemTest1.getIsItemAvailable(), "ItemDto->Item не совпадает доступность");
    }
}
