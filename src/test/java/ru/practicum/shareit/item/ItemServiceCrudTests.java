package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingJpaRepository;
import ru.practicum.shareit.user.*;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class ItemServiceCrudTests {
    private static final ItemJpaRepository mockItemJpaRepository = Mockito.mock(ItemJpaRepository.class);
    private static final ItemMapper mockItemMapper = Mockito.mock(ItemMapper.class);
    private final CommentJpaRepository mockCommentJpaRepository = Mockito.mock(CommentJpaRepository.class);
    private final CommentMapper mockCommentMapper = Mockito.mock(CommentMapper.class);
    private static final UserJpaRepository mockUserJpaRepository = Mockito.mock(UserJpaRepository.class);
    private final BookingJpaRepository mockBookingJpaRepository = Mockito.mock(BookingJpaRepository.class);
    private final ItemService itemService = new ItemService(mockItemJpaRepository, mockItemMapper, mockCommentJpaRepository,
            mockCommentMapper, mockUserJpaRepository, mockBookingJpaRepository);

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

    private final ItemDto itemDto1 = ItemDto.builder()
            .itemId(1L)
            .itemName("Item1")
            .itemDescription("Item1 desc")
            .isItemAvailable(true)
            .build();

    @BeforeAll
    static void whenThenMockingSetup() {
        when(mockUserJpaRepository.existsById(Mockito.anyLong())).thenReturn(true);
        when(mockItemJpaRepository.existsById(Mockito.anyLong())).thenReturn(true);
        when(mockItemMapper.dtoToItem(any(ItemDto.class), any())).thenReturn(item1);
    }

    @AfterEach
    void resetMockCount() {
        Mockito.clearInvocations(mockItemJpaRepository);
    }

    @Test
    void itemServiceCreateTests() {
        itemService.createEntityService(itemDto1, 1L);
        Mockito.verify(mockItemJpaRepository, Mockito.times(1)).save(any(Item.class));
    }

    @Test
    void itemServiceCreateCommentTests() {
        when(mockBookingJpaRepository.findAllByBookerIdAndItemIdAndTime(any(), any(), any())).thenReturn(List.of(new Booking()));
        itemService.createCommentService(CommentDto.builder().commentText("test").build(), 1L, 1L);
        Mockito.verify(mockCommentJpaRepository, Mockito.times(1)).save(any());
    }

    @Test
    void itemServiceReadTests() {
        //getOneById
        when(mockItemJpaRepository.getReferenceById(Mockito.anyLong())).thenReturn(item1);
        itemService.getEntityService(2L, 1L);
        Mockito.verify(mockItemJpaRepository, Mockito.times(2)).getReferenceById(2L);

        //getAllByOwnerId
        when(mockItemJpaRepository.findAllByOwnerUserId(any(), any())).thenReturn(Page.empty());
        itemService.getEntityService(null, 1L, "0", "10");
        Mockito.verify(mockItemJpaRepository, Mockito.times(1)).findAllByOwnerUserId(any(), any());
    }

    @Test
    void itemServiceReadBySearchTextTests() {
        when(mockItemJpaRepository.findAllAvailableItemsBySearchText(any(), any())).thenReturn(Page.empty());
        itemService.getItemBySearchText("1", "0", "10");
        Mockito.verify(mockItemJpaRepository, Mockito.times(1)).findAllAvailableItemsBySearchText(any(), any());
    }

    @Test
    void itemServiceUpdateTests() {
        itemService.updateEntityService(1L, itemDto1, 1L);
        Mockito.verify(mockItemJpaRepository, Mockito.times(1)).save(any(Item.class));
    }

    @Test
    void itemServiceDeleteTests() {
        //deleteOneById
        itemService.deleteEntityService(3L);
        Mockito.verify(mockItemJpaRepository, Mockito.times(1)).deleteById(3L);

        //deleteAll
        itemService.deleteEntityService(null);
        Mockito.verify(mockItemJpaRepository, Mockito.times(1)).deleteAll();
    }
}
