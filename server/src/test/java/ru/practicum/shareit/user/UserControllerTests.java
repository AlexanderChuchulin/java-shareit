package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTests {
    @MockBean
    private UserService mockUserService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    private final UserDto userDto1 = UserDto.builder()
            .userId(1L)
            .userName("UserDto1")
            .email("UserDto1@test.ru")
            .build();

    private final UserDto userDto2 = UserDto.builder()
            .userId(2L)
            .userName("UserDto2")
            .email("UserDto2@test.ru")
            .build();

    @Test
    @SneakyThrows
    void createUserControllerTests() {
        when(mockUserService.createEntityService(any(), any()))
                .thenReturn(userDto1);

        mockMvc.perform(post("/users")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto1.getUserId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto1.getUserName())))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail())));
    }

    @Test
    @SneakyThrows
    void getUserByIdControllerTests() {
        when(mockUserService.getEntityService(any(), any(), any()))
                .thenReturn(userDto2);

        mockMvc.perform(get("/users/2")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto2.getUserId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto2.getUserName())))
                .andExpect(jsonPath("$.email", is(userDto2.getEmail())));
    }

    @Test
    @SneakyThrows
    void getAllUserControllerTests() {
        when(mockUserService.getEntityService(any(), any(), any()))
                .thenReturn(List.of(userDto1, userDto2));

        mockMvc.perform(get("/users")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(userDto1.getUserId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto1.getUserName())))
                .andExpect(jsonPath("$[0].email", is(userDto1.getEmail())))
                .andExpect(jsonPath("$[1].id", is(userDto2.getUserId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(userDto2.getUserName())))
                .andExpect(jsonPath("$[1].email", is(userDto2.getEmail())));
    }

    @Test
    @SneakyThrows
    void updateUserControllerTests() {
        UserDto userDtoUpdate1 = UserDto.builder()
                .userId(1L)
                .userName("UserDtoUpdate1")
                .email("UserDtoUpdate1@test.ru")
                .build();

        when(mockUserService.updateEntityService(any(), any(), any()))
                .thenReturn(userDtoUpdate1);

        mockMvc.perform(patch("/users/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(userDtoUpdate1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoUpdate1.getUserId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoUpdate1.getUserName())))
                .andExpect(jsonPath("$.email", is(userDtoUpdate1.getEmail())));
    }

    @Test
    @SneakyThrows
    void deleteUserByIdControllerTests() {
        mockMvc.perform(delete("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(mockUserService, Mockito.times(1)).deleteEntityService(1L);
    }

    @Test
    @SneakyThrows
    void deleteAllUsersControllerTests() {
        mockMvc.perform(delete("/users/")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(mockUserService, Mockito.times(1)).deleteEntityService(null);
    }
}
