package ru.practicum.shareit.request;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class RequestIntegrationTests {
    @Autowired
    private RequestService requestService;
    @Autowired
    private RequestJpaRepository requestJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;

    private final User user1 = User.builder()
            .userId(1L)
            .userName("Requestor1")
            .email("Requestor1@test.ru")
            .userIdHeader(1L)
            .build();

    private final User user2 = User.builder()
            .userId(2L)
            .userName("Owner2")
            .email("Owner2@test.ru")
            .userIdHeader(2L)
            .build();

    @Test
    @SneakyThrows
    void requestIntegrationTests() {
        userJpaRepository.save(user1);
        userJpaRepository.save(user2);

        RequestDto requestDto1 = RequestDto.builder()
                .requestDescription("request1 description")
                .requestDate(LocalDateTime.now())
                .userIdHeader(1L)
                .build();

        RequestDto requestDto2 = RequestDto.builder()
                .requestDescription("request2 description")
                .requestDate(LocalDateTime.now())
                .userIdHeader(1L)
                .build();

        RequestDto requestDto3 = RequestDto.builder()
                .requestDescription("request3 description")
                .requestDate(LocalDateTime.now())
                .userIdHeader(2L)
                .build();

        RequestDto requestDto4 = RequestDto.builder()
                .requestDescription("request4 description")
                .requestDate(LocalDateTime.now())
                .userIdHeader(2L)
                .build();

        // *Create
        assertEquals(0, requestJpaRepository.count(), "Перед тестами в БД запросов есть записи");
        assertNull(requestDto1.getRequestId(), "requestDto id не null");
        RequestDto requestDtoTest1 = requestService.createEntityService(requestDto1, requestDto1.getUserIdHeader());
        assertNotNull(requestDtoTest1.getRequestId(), "После создания запроса в БД ему не присвоен id");
        assertEquals(1, requestJpaRepository.count(), "После создания запроса в БД не 1 запись");
        Thread.sleep(10);
        RequestDto requestDtoTest2 = requestService.createEntityService(requestDto2, requestDto2.getUserIdHeader());
        assertEquals(2, requestJpaRepository.count(), "После создания запроса в БД не 2 записи");
        Thread.sleep(10);
        RequestDto requestDtoTest3 = requestService.createEntityService(requestDto3, requestDto3.getUserIdHeader());
        Thread.sleep(10);
        RequestDto requestDtoTest4 = requestService.createEntityService(requestDto4, requestDto4.getUserIdHeader());

        // *Read
        //getOneById
        assertEquals(requestDtoTest2, requestService.getEntityService(2L, 1L),
                "Получить запрос по id. Запросы не совпадают");

        //getAllByRequestorId
        assertEquals(List.of(requestDtoTest2, requestDtoTest1), requestService
                        .getEntityService(null, 1L, "0", "10"),
                "Получить все запросы по id null по userIdHeader владельца, списки не совпадают");

        //getAllWithPagination
        assertEquals(List.of(requestDtoTest4, requestDtoTest3), requestService
                        .getEntityService(-444555666L, 1L, "0", "10"),
                "Получить все запросы по id -444555666L, списки не совпадают");
    }
}
