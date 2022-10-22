package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class RequestMappingTests {
    private final Item mockItem = Mockito.mock(Item.class);
    private final UserJpaRepository mockUserJpaRepository = Mockito.mock(UserJpaRepository.class);
    private final ItemJpaRepository mockItemJpaRepository = Mockito.mock(ItemJpaRepository.class);
    private final RequestMapper requestMapper = new RequestMapper(mockUserJpaRepository, mockItemJpaRepository);

    private final Request request1 = Request.builder()
            .requestId(1L)
            .requestDescription("request1 description")
            .requestor(User.builder().userId(1L).build())
            .requestDate(LocalDateTime.now())
            .userIdHeader(1L)
            .build();

    @Test
    void requestToDtoTests() {


        RequestDto requestDtoTest1 = requestMapper.requestToDto(request1);

        assertEquals(request1.getRequestId(), requestDtoTest1.getRequestId(), "Request->RequestDto не совпадает id");
        assertEquals(request1.getRequestDescription(), requestDtoTest1.getRequestDescription(),
                "Request->RequestDto не совпадает description");
        assertEquals(request1.getRequestDate().getMinute(), requestDtoTest1.getRequestDate().getMinute(),
                "Request->RequestDto не совпадает RequestDate");
    }

    @Test
    void dtoToRequestTests() {
        when(mockItem.getRequest()).thenReturn(request1);

        RequestDto requestDto1 = RequestDto.builder()
                .requestId(1L)
                .requestDescription("request1 description")
                .requestDate(LocalDateTime.now())
                .itemsDtoForRequestList(RequestDto.ItemDtoForRequest.createItemsDtoForRequestList(List.of(mockItem)))
                .userIdHeader(1L)
                .build();

        Request requestTest1 = requestMapper.dtoToRequest(requestDto1, 1L);

        assertEquals(requestDto1.getRequestId(), requestTest1.getRequestId(), "RequestDto->Request не совпадает id");
        assertEquals(requestDto1.getRequestDescription(), requestTest1.getRequestDescription(),
                "Request->RequestDto не совпадает description");
        assertEquals(requestDto1.getRequestDate().getMinute(), requestTest1.getRequestDate().getMinute(),
                "Request->RequestDto не совпадает RequestDate");
        assertEquals(requestDto1.getUserIdHeader(), requestTest1.getUserIdHeader(),
                "Request->RequestDto не совпадает UserIdHeader");
    }
}
