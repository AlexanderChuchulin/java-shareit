package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserMappingTests {
    private final UserMapper userMapper = new UserMapper();

    @Test
    void userToDtoTests() {
        User user1 = User.builder()
                .userId(1L)
                .userName("User1")
                .email("User1@test.ru")
                .userIdHeader(1L)
                .build();

        UserDto userDtoTest1 = userMapper.userToDto(user1);

        assertEquals(user1.getUserId(), userDtoTest1.getUserId(), "User->UserDto не совпадает id");
        assertEquals(user1.getUserName(), userDtoTest1.getUserName(), "User->UserDto не совпадает name");
        assertEquals(user1.getEmail(), userDtoTest1.getEmail(), "User->UserDto не совпадает email");
    }

    @Test
    void dtoToUserTests() {
        UserDto userDto1 = UserDto.builder()
                .userId(1L)
                .userName("UserDto1")
                .email("UserDto1@test.ru")
                .build();

        User userTest1 = userMapper.dtoToUser(userDto1);

        assertNull(userTest1.getUserId(), "UserDto->User id не null");
        assertEquals(userDto1.getUserName(), userTest1.getUserName(), "UserDto->User не совпадает name");
        assertEquals(userDto1.getEmail(), userTest1.getEmail(), "UserDto->User не совпадает email");
    }
}
