package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.exception.EntityNotFoundExc;
import ru.practicum.shareit.exception.MainPropDuplicateExc;
import ru.practicum.shareit.exception.ValidationExc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class UserServiceLogicTests {
    private final UserJpaRepository mockUserJpaRepository = Mockito.mock(UserJpaRepository.class);
    private final UserMapper mockUserMapper = Mockito.mock(UserMapper.class);
    private final UserService userService = new UserService(mockUserMapper, mockUserJpaRepository);

    private final User user1 = User.builder()
            .userId(1L)
            .userName("User1")
            .email("User1@test.ru")
            .build();

    @Test
    void userServiceLogicTests() {
        user1.setUserName(null);
        userService.validateUserService(user1, false, "");
        assertEquals(user1.getEmail(), user1.getUserName(), "Если имя пользователя null, оно не совпадает c email");

        user1.setUserName(" ");
        userService.validateUserService(user1, false, "");
        assertEquals(user1.getEmail(), user1.getUserName(), "Если имя пользователя пусто, оно не совпадает c email");

        user1.setEmail(null);
        assertThrows(ValidationExc.class, () -> userService.validateUserService(user1, false, ""),
                "Если поле email null не брошено исключение ValidationExc");

        user1.setEmail("ru.test@wrongmail");
        assertThrows(ValidationExc.class, () -> userService.validateUserService(user1, false, ""),
                "Если формат email неправильный не брошено исключение ValidationExc");

        User user2 = User.builder()
                .userId(2L)
                .userName("User2")
                .email("User1@test.ru")
                .build();
        when(mockUserJpaRepository.findByEmailContainingIgnoreCase(Mockito.anyString())).thenReturn(user1);
        assertThrows(MainPropDuplicateExc.class, () -> userService.validateUserService(user2, true, ""),
                "При обновлении пользователя с существующим email не брошено исключение MainPropDuplicateExc");

        when(mockUserJpaRepository.existsById(-1L)).thenReturn(false);
        assertThrows(EntityNotFoundExc.class, () -> userService.userExistCheck(-1L, ""),
                "При поиске несуществующего пользователя не брошено исключение EntityNotFoundExc");
    }
}
