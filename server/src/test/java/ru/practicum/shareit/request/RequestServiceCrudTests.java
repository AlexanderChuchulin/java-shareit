package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class RequestServiceCrudTests {
    private static final RequestJpaRepository mockRequestJpaRepository = Mockito.mock(RequestJpaRepository.class);
    private static final RequestMapper mockRequestMapper = Mockito.mock(RequestMapper.class);
    private static final UserJpaRepository mockUserJpaRepository = Mockito.mock(UserJpaRepository.class);
    private final RequestService requestService = new RequestService(mockRequestJpaRepository, mockRequestMapper, mockUserJpaRepository);

    private static final Request request1 = Request.builder()
            .requestId(1L)
            .requestDescription("request1 description")
            .requestor(User.builder().userId(1L).build())
            .requestDate(LocalDateTime.now())
            .userIdHeader(1L)
            .build();

    private final Item item1 = Item.builder()
            .itemId(1L)
            .itemName("Item1")
            .itemDescription("Item1 desc")
            .isItemAvailable(true)
            .owner(new User())
            .userIdHeader(1L)
            .request(request1)
            .build();

    private final RequestDto requestDto1 = RequestDto.builder()
            .requestId(1L)
            .requestDescription("request1 description")
            .requestDate(LocalDateTime.now())
            .itemsDtoForRequestList(RequestDto.ItemDtoForRequest.createItemsDtoForRequestList(List.of(item1)))
            .userIdHeader(1L)
            .build();

    @BeforeAll
    static void whenThenMockingSetup() {
        when(mockUserJpaRepository.existsById(Mockito.anyLong())).thenReturn(true);
        when(mockRequestJpaRepository.existsById(Mockito.anyLong())).thenReturn(true);
        when(mockRequestMapper.dtoToRequest(any(RequestDto.class), any())).thenReturn(request1);
    }

    @Test
    void requestServiceCreateTests() {
        requestService.createEntityService(requestDto1, 1L);
        Mockito.verify(mockRequestJpaRepository, Mockito.times(1)).save(any(Request.class));
    }

    @Test
    void requestServiceReadTests() {
        //getOneById
        when(mockRequestJpaRepository.getReferenceById(Mockito.anyLong())).thenReturn(any(Request.class));
        requestService.getEntityService(2L, 1L);
        Mockito.verify(mockRequestJpaRepository, Mockito.times(1)).getReferenceById(2L);

        //getAllByRequestorId
        when(mockRequestJpaRepository.findAllByRequestorUserIdOrderByRequestDateDesc(Mockito.anyLong())).thenReturn(List.of());
        requestService.getEntityService(null, 1L, "0", "10");
        Mockito.verify(mockRequestJpaRepository, Mockito.times(1)).findAllByRequestorUserIdOrderByRequestDateDesc(Mockito.anyLong());

        //getAllWithPagination
        when(mockRequestJpaRepository.findAllByRequestorUserIdNotOrderByRequestDateDesc(Mockito.anyLong(), any())).thenReturn(Page.empty());
        requestService.getEntityService(-444555666L, 1L, "0", "10");
        Mockito.verify(mockRequestJpaRepository, Mockito.times(1)).findAllByRequestorUserIdOrderByRequestDateDesc(Mockito.anyLong());
    }

    @Test
    void requestServiceUpdateTests() {
        requestService.updateEntityService(1L, requestDto1, 1L);
        Mockito.verify(mockRequestJpaRepository, Mockito.never()).save(any(Request.class));
    }

    @Test
    void requestServiceDeleteTests() {
        //deleteOneById
        requestService.deleteEntityService(3L);
        Mockito.verify(mockRequestJpaRepository, Mockito.times(1)).deleteById(3L);

        //deleteAll
        requestService.deleteEntityService(null);
        Mockito.verify(mockRequestJpaRepository, Mockito.times(1)).deleteAll();
    }
}
