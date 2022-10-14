package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.BookingJpaRepository;
import ru.practicum.shareit.exception.AuthorizationExc;
import ru.practicum.shareit.exception.EntityNotFoundExc;
import ru.practicum.shareit.exception.MainPropDuplicateExc;
import ru.practicum.shareit.exception.ValidationExc;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserJpaRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ItemServiceLogicTests {
    private final ItemJpaRepository mockItemJpaRepository = Mockito.mock(ItemJpaRepository.class);
    private final ItemMapper mockItemMapper = Mockito.mock(ItemMapper.class);
    private final CommentJpaRepository mockCommentJpaRepository = Mockito.mock(CommentJpaRepository.class);
    private final CommentMapper mockCommentMapper = Mockito.mock(CommentMapper.class);
    private final UserJpaRepository mockUserJpaRepository = Mockito.mock(UserJpaRepository.class);
    private final BookingJpaRepository mockBookingJpaRepository = Mockito.mock(BookingJpaRepository.class);
    private final ItemService itemService = new ItemService(mockItemJpaRepository, mockItemMapper, mockCommentJpaRepository,
            mockCommentMapper, mockUserJpaRepository, mockBookingJpaRepository);

    private final Item item1 = Item.builder()
            .itemId(1L)
            .itemName("Item1")
            .itemDescription("Item1 desc")
            .isItemAvailable(true)
            .owner(User.builder().userId(1L).build())
            .build();

    @Test
    void itemServiceLogicTests() {
        assertThrows(MainPropDuplicateExc.class, () -> itemService.validateItemService(item1, false, ""),
                "Если userIdHeader null не брошено исключение MainPropDuplicateExc");

        item1.setUserIdHeader(-1L);
        when(mockUserJpaRepository.existsById(-1L)).thenReturn(false);
        assertThrows(EntityNotFoundExc.class, () -> itemService.validateItemService(item1, false, ""),
                "Если userIdHeader не найден не брошено исключение EntityNotFoundExc");

        item1.setUserIdHeader(2L);
        when(mockUserJpaRepository.existsById(2L)).thenReturn(true);
        when(mockItemJpaRepository.getReferenceById(any())).thenReturn(item1);
        assertThrows(AuthorizationExc.class, () -> itemService.validateItemService(item1, true, ""),
                "Если при обновлении вещи userIdHeader не соответствует id владельца не брошено исключение AuthorizationExc");

        item1.setItemName("");
        item1.setItemDescription("");
        item1.setIsItemAvailable(null);
        assertThrows(ValidationExc.class, () -> itemService.validateItemService(item1, false, ""),
                "Если при создании вещи не заданы имя, описание и доступность не брошено исключение ValidationExc");

        item1.setUserIdHeader(1L);
        when(mockUserJpaRepository.existsById(1L)).thenReturn(true);
        ValidationExc validationExc = assertThrows(ValidationExc.class, () -> itemService.validateItemService(item1, false, ""),
                "Если при обновлении вещи не заданы имя, описание и доступность не брошено исключение ValidationExc");
        assertTrue(Stream.of("название", "описание", "доступность").allMatch(validationExc.getMessage().toLowerCase()::contains),
                "Сообщение об исключении ValidationExc не описывает причины: название, описание, доступность");

        when(mockItemJpaRepository.existsById(-1L)).thenReturn(false);
        assertThrows(EntityNotFoundExc.class, () -> itemService.itemExistCheck(-1L, ""),
                "При поиске несуществующей вещи не брошено исключение EntityNotFoundExc");

        when(mockBookingJpaRepository.findAllByBookerIdAndItemIdAndTime(any(), any(), any())).thenReturn(List.of());
        assertThrows(ValidationExc.class, () -> itemService.createCommentService(any(), any(), any()),
                "При создании комментария от пользователя без бронирований не брошено исключение ValidationExc");

        assertThrows(ValidationExc.class, () -> itemService.createCommentService(
                new CommentDto.CommentDtoBuilder().commentText(" ").build(), 1L, 1L),
                "При создании пустого комментария не брошено исключение ValidationExc");

    }
}
