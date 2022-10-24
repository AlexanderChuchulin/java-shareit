package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ItemIntegrationTests {
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private ItemJpaRepository itemJpaRepository;
    @Autowired
    private CommentJpaRepository commentJpaRepository;
    @Autowired
    private ItemService itemService;
    @MockBean
    private BookingJpaRepository mockBookingJpaRepository;

    @Test
    void itemIntegrationTests() {
        when(mockBookingJpaRepository.findAllByBookerIdAndItemIdAndTime(any(), any(), any())).thenReturn(List.of(new Booking()));

        User user1 = User.builder()
                .userId(1L)
                .userName("User1")
                .email("User1@test.ru")
                .userIdHeader(1L)
                .build();

        userJpaRepository.save(user1);

        ItemDto itemDto1 = ItemDto.builder()
                .itemName("Item1Dto1")
                .itemDescription("Item1Dto1 description")
                .isItemAvailable(true)
                .userIdHeader(1L)
                .build();

        ItemDto itemDto2 = ItemDto.builder()
                .itemId(2L)
                .itemName("Item1Dto2")
                .itemDescription("Item1Dto2 description SEARCH ME")
                .isItemAvailable(true)
                .userIdHeader(1L)
                .build();

        CommentDto commentDto1 = CommentDto.builder()
                .commentText("commentDto1 text")
                .commentDate(LocalDateTime.now())
                .authorName("commentDto1Author")
                .itemId(1L)
                .userIdHeader(1L)
                .build();

        // *Create
        assertEquals(0, itemJpaRepository.count(), "Перед тестами в БД вещей есть записи");
        assertNull(itemDto1.getItemId(), "itemDto id не null");
        ItemDto itemDtoTest1 = itemService.createEntityService(itemDto1, 1L);
        assertNotNull(itemDtoTest1.getItemId(), "После создания вещи в БД ей не присвоен id");
        assertEquals(1, itemJpaRepository.count(), "После создания вещи в БД не 1 запись");
        ItemDto itemDtoTest2 = itemService.createEntityService(itemDto2, 1L);
        assertEquals(2, itemJpaRepository.count(), "После создания вещи в БД не 2 записи");

        // *Create Comment
        assertEquals(0, commentJpaRepository.count(), "Перед тестами в БД комментов есть записи");
        assertNull(commentDto1.getCommentId(), "commentDto id не null");
        assertTrue(itemDtoTest1.getCommentsDtoForItemList().isEmpty(), "comments не пусто");
        CommentDto commentDtoTest1 = itemService.createCommentService(commentDto1, 1L, 1L);
        Comment commentTest1 = commentJpaRepository.getReferenceById(1L);
        assertNotNull(commentDtoTest1.getCommentId(), "После создания коммента в БД ему не присвоен id");
        assertEquals(1, commentJpaRepository.count(), "После создания коммента в БД не 1 запись");
        itemDtoTest1.setCommentsDtoForItemList(ItemDto.CommentDtoForItem.createCommentsDtoForItemList(List.of(commentTest1)));
        assertFalse(itemDtoTest1.getCommentsDtoForItemList().isEmpty(), "comments пусто");

        // *Read
        //getOneById
        assertEquals(itemDtoTest2, itemService.getEntityService(2L, 1L),
                "Получить вещь по id. Вещи не совпадают");

        //getAllByOwnerId
        assertEquals(List.of(itemDtoTest1, itemDtoTest2), itemService.getEntityService(null, 1L, "0", "10"),
                "Получить все вещи по id null по userIdHeader владельца, списки не совпадают");

        //getAllBySearchText
        assertEquals(List.of(itemDtoTest2), itemService.getItemBySearchText("sEarCh","0", "10"),
                "Получить все вещи по поиску текста, списки не совпадают");

        // *JPA
        assertEquals(List.of(), itemJpaRepository.findAllByRequestRequestId(-1L),
                "Получить все вещи по несуществующему id запроса, возвращает не пустой список");
    }
}
