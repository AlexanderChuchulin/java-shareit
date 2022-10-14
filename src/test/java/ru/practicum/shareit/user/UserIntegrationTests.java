package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class UserIntegrationTests {
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserService userService;

    @Test
    void userIntegrationTests() {
        UserDto userDto1 = UserDto.builder()
                .userId(1L)
                .userName("UserDto1")
                .email("UserDto1@test.ru")
                .build();

        UserDto userDto2 = UserDto.builder()
                .userId(2L)
                .userName("UserDto2")
                .email("UserDto2@test.ru")
                .build();

        // *Create
        assertEquals(0, userJpaRepository.count(), "Перед тестами в БД пользователей есть записи");
        UserDto userDtoTest1 = userService.createEntityService(userDto1, -1L);
        assertNotNull(userDtoTest1.getUserId(), "После создания пользователя в БД ему не присвоен id");
        assertEquals(1, userJpaRepository.count(), "После создания пользователя в БД не 1 запись");
        UserDto userDtoTest2 = userService.createEntityService(userDto2, -1L);
        assertEquals(2, userJpaRepository.count(), "После создания пользователя в БД не 2 записи");

        // *Read
        //getOneById
        assertEquals(userDtoTest2, userService.getEntityService(2L, -1L),
                "Получить пользователя по id. Пользователи не совпадают");

        //getAll
        assertEquals(List.of(userDto1, userDto2), userService.getEntityService(null, -1L),
                "Получить всех пользователей по id null, списки не совпадают");

        // *JPA
        assertThrows(RuntimeException.class, () -> userService.createEntityService(userDto1, -1L),
                "При создании нового пользователя с существующим email не брошено исключение");

        User userTest1 = userJpaRepository.findByEmailContainingIgnoreCase("USERDTO1@TEST.RU");
        assertNotNull(userTest1, "Поиск в репозитории по email с изменённым регистром, не вернул объект");

        assertEquals(userDto1, userMapper.userToDto(userTest1), "Поиск в БД по email. Пользователи не совпадают");
        assertEquals(userDtoTest1.getUserId(), userTest1.getUserId(), "Поиск в БД по email. Не совпадает id");
        assertEquals(userDtoTest1.getUserName(), userTest1.getUserName(), "Поиск в БД по email. Не совпадает name");
        assertEquals(userDtoTest1.getEmail(), userTest1.getEmail(), "Поиск в БД по email. Не совпадает email");
    }
}
