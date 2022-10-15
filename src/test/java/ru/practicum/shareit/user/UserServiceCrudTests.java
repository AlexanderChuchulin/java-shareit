package ru.practicum.shareit.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserServiceCrudTests {
    private static final UserJpaRepository mockUserJpaRepository = Mockito.mock(UserJpaRepository.class);
    private final UserService userService = new UserService(new UserMapper(), mockUserJpaRepository);

    private static final User user1 = User.builder()
            .userId(1L)
            .userName("User1")
            .email("User1@test.ru")
            .build();

    private final UserDto userDto1 = UserDto.builder()
            .userId(1L)
            .userName("UserDto1")
            .email("UserDto1@test.ru")
            .build();

    @BeforeAll
    static void whenThenMockingSetup() {
        when(mockUserJpaRepository.existsById(Mockito.anyLong())).thenReturn(true);
        when(mockUserJpaRepository.getReferenceById(Mockito.anyLong())).thenReturn(user1);
        when(mockUserJpaRepository.save(any(User.class))).thenReturn(user1);
    }

    @AfterEach
    void resetMockCount() {
        Mockito.clearInvocations(mockUserJpaRepository);
    }

    @Test
    void userServiceCreateTests() {
        userService.createEntityService(userDto1, -1L);
        Mockito.verify(mockUserJpaRepository, Mockito.times(1)).save(any(User.class));
    }

    @Test
    void userServiceReadTests() {
        //getOneById
        userService.getEntityService(2L, -1L);
        Mockito.verify(mockUserJpaRepository, Mockito.times(1)).getReferenceById(2L);

        //getAll
        userService.getEntityService(null, -1L);
        Mockito.verify(mockUserJpaRepository, Mockito.times(1)).findAll();
    }

    @Test
    void userServiceUpdateTests() {
        userService.updateEntityService(1L, userDto1, 1L);
        Mockito.verify(mockUserJpaRepository, Mockito.times(1)).save(any(User.class));
    }

    @Test
    void userServiceDeleteTests() {
        //deleteOneById
        userService.deleteEntityService(3L);
        Mockito.verify(mockUserJpaRepository, Mockito.times(1)).deleteById(3L);

        //deleteAll
        userService.deleteEntityService(null);
        Mockito.verify(mockUserJpaRepository, Mockito.times(1)).deleteAll();
    }
}
