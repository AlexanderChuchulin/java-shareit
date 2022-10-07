package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.EntityNotFoundExc;
import ru.practicum.shareit.exception.MainPropDuplicateExc;
import ru.practicum.shareit.exception.ValidationExc;
import ru.practicum.shareit.user.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
class ShareItTests {
    @Autowired
    private UserJpaRepository userJpaRepository;

    @Mock
    private UserService mockUserService;

    @InjectMocks
    private UserController userController;

    /**
     * Тесты UserService
     */
    @Test
    void userServiceLogicTests() {
        UserJpaRepository mockUserJpaRepository = Mockito.mock(UserJpaRepository.class);
        UserMapper mockUserMapper = Mockito.mock(UserMapper.class);
        UserService userService = new UserService(mockUserMapper, mockUserJpaRepository);

        User user1 = User.builder()
                .userId(1L)
                .userName("User1")
                .email("User1@test.ru")
                .build();

        user1.setUserName(null);
        userService.validateEntityService(user1, false, "");
        assertEquals(user1.getEmail(), user1.getUserName(), "Если имя пользователя null, оно не совпадает c email");

        user1.setUserName(" ");
        userService.validateEntityService(user1, false, "");
        assertEquals(user1.getEmail(), user1.getUserName(), "Если имя пользователя пусто, оно не совпадает c email");

        user1.setEmail(null);
        assertThrows(ValidationExc.class, () -> userService.validateEntityService(user1, false, ""),
                "Если поле email null не брошено исключение ValidationExc");

        user1.setEmail("ru.test@wrongmail");
        assertThrows(ValidationExc.class, () -> userService.validateEntityService(user1, false, ""),
                "Если формат email неправильный не брошено исключение ValidationExc");

        User user2 = User.builder()
                .userId(2L)
                .userName("User2")
                .email("User1@test.ru")
                .build();
        when(mockUserJpaRepository.findByEmailContainingIgnoreCase(Mockito.anyString())).thenReturn(user1);
        assertThrows(MainPropDuplicateExc.class, () -> userService.validateEntityService(user2, true, ""),
                "При обновлении пользователя с существующим email не брошено исключение MainPropDuplicateExc");

        when(mockUserJpaRepository.existsById(-1L)).thenReturn(false);
        assertThrows(EntityNotFoundExc.class, () -> userService.entityExistCheck(-1L, ""),
                "При поиске несуществующего объекта не брошено исключение EntityNotFoundExc");
    }

    @Test
    void userMappingTests() {
        UserMapper userMapper = new UserMapper();

        UserDto userDto1 = UserDto.builder()
                .userId(1L)
                .userName("UserDto1")
                .email("UserDto1@test.ru")
                .build();
        User userTest1 = userMapper.dtoToUser(userDto1);
        assertNull(userTest1.getUserId(), "UserDto->User id не null");
        assertEquals(userDto1.getUserName(), userTest1.getUserName(), "UserDto->User не совпадает name");
        assertEquals(userDto1.getEmail(), userTest1.getEmail(), "UserDto->User не совпадает email");

        User user1 = User.builder()
                .userId(1L)
                .userName("User1")
                .email("User1@test.ru")
                .build();
        UserDto userDtoTest1 = userMapper.userToDto(user1);
        assertEquals(user1.getUserId(), userDtoTest1.getUserId(), "User->UserDto не совпадает id");
        assertEquals(user1.getUserName(), userDtoTest1.getUserName(), "User->UserDto не совпадает name");
        assertEquals(user1.getEmail(), userDtoTest1.getEmail(), "User->UserDto не совпадает email");
    }

    @Test
    void userServiceCrudTests() {
        UserJpaRepository mockUserJpaRepository = Mockito.mock(UserJpaRepository.class);
        UserService userService = new UserService(new UserMapper(), mockUserJpaRepository);

        User user1 = User.builder()
                .userId(1L)
                .userName("User1")
                .email("User1@test.ru")
                .build();

        UserDto userDto1 = UserDto.builder()
                .userId(1L)
                .userName("UserDto1")
                .email("UserDto1@test.ru")
                .build();

        when(mockUserJpaRepository.existsById(Mockito.anyLong())).thenReturn(true);
        when(mockUserJpaRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user1));
        when(mockUserJpaRepository.getReferenceById(Mockito.anyLong())).thenReturn(user1);

        // *Create
        when(mockUserJpaRepository.save(any(User.class))).thenReturn(user1);
        userService.createEntityService(userDto1, -1L);
        Mockito.verify(mockUserJpaRepository, Mockito.atLeastOnce()).save(any(User.class));

        // *Read
        //getOneById
        userService.getEntityService(2L, -1L);
        Mockito.verify(mockUserJpaRepository, Mockito.atLeastOnce()).findById(2L);

        //getAll
        userService.getEntityService(null, -1L);
        Mockito.verify(mockUserJpaRepository, Mockito.atLeastOnce()).findAll();

        // *Update
        userService.updateEntityService(1L, userDto1, 1L);
        Mockito.verify(mockUserJpaRepository, Mockito.atLeastOnce()).save(any(User.class));

        // *Delete

        //deleteOneById
        userService.deleteEntityService(3L);
        Mockito.verify(mockUserJpaRepository, Mockito.atLeastOnce()).deleteById(3L);

        //deleteAll
        userService.deleteEntityService(null);
        Mockito.verify(mockUserJpaRepository, Mockito.atLeastOnce()).deleteAll();
    }

    @Test
    void userIntegrationAndJpaRepositoryTests() {
        UserMapper userMapper = new UserMapper();
        UserService userService = new UserService(userMapper, userJpaRepository);

        UserDto userDto1 = UserDto.builder()
                .userName("UserDto1")
                .email("UserDto1@test.ru")
                .build();

        UserDto userDto2 = UserDto.builder()
                .userName("UserDto2")
                .email("UserDto2@test.ru")
                .build();

        // *Create
        assertEquals(0, userJpaRepository.count(), "Перед тестами в БД пользователей есть записи");
        assertNull(userDto1.getUserId(), "UserDto id не null");
        UserDto userDtoTest1 = userService.createEntityService(userDto1, -1L);
        userDto1.setUserId(userDtoTest1.getUserId());
        assertNotNull(userDtoTest1.getUserId(), "После создания пользователя в БД ему не присвоен id");
        assertEquals(1, userJpaRepository.count(), "После создания пользователя в БД не 1 запись");
        UserDto userDtoTest2 = userService.createEntityService(userDto2, -1L);
        userDto2.setUserId(userDtoTest2.getUserId());
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

    @Test
    @SneakyThrows
    void userControllerTests() {
        ObjectMapper objectMapper = new ObjectMapper();
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();


        // *Create
        UserDto userDto1 = UserDto.builder()
                .userId(1L)
                .userName("UserDto1")
                .email("UserDto1@test.ru")
                .build();

        when(mockUserService.createEntityService(any(), any()))
                .thenReturn(userDto1);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto1.getUserId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto1.getUserName())))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail())));

    }


}
