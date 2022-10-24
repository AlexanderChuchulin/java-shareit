package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.exception.EntityNotFoundExc;
import ru.practicum.shareit.exception.MainPropDuplicateExc;
import ru.practicum.shareit.exception.ValidationExc;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserJpaRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class RequestServiceLogicTests {
    private final RequestJpaRepository mockRequestJpaRepository = Mockito.mock(RequestJpaRepository.class);
    private final RequestMapper mockRequestMapper = Mockito.mock(RequestMapper.class);
    private final UserJpaRepository mockUserJpaRepository = Mockito.mock(UserJpaRepository.class);
    private final RequestService requestService = new RequestService(mockRequestJpaRepository, mockRequestMapper, mockUserJpaRepository);

    private final Request request1 = Request.builder()
            .requestId(1L)
            .requestDescription("request1 description")
            .requestor(User.builder().userId(1L).build())
            .requestDate(LocalDateTime.now())
            .build();

    @Test
    void requestServiceLogicTests() {
        assertThrows(MainPropDuplicateExc.class, () -> requestService.validateRequestService(request1, ""),
                "Если userIdHeader null не брошено исключение MainPropDuplicateExc");

        request1.setUserIdHeader(-1L);
        when(mockUserJpaRepository.existsById(-1L)).thenReturn(false);
        assertThrows(EntityNotFoundExc.class, () -> requestService.validateRequestService(request1, ""),
                "Если userIdHeader не найден не брошено исключение EntityNotFoundExc");

        when(mockUserJpaRepository.existsById(any())).thenReturn(true);
        request1.setRequestDescription(" ");
        assertThrows(ValidationExc.class, () -> requestService.validateRequestService(request1, ""),
                "Если при создании запроса не задано описание не брошено исключение ValidationExc");

        when(mockRequestJpaRepository.existsById(-1L)).thenReturn(false);
        assertThrows(EntityNotFoundExc.class, () -> requestService.requestExistCheck(-1L, ""),
                "При поиске несуществующего запроса не брошено исключение EntityNotFoundExc");
    }
}
