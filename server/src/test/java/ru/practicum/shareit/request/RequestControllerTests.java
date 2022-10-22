package ru.practicum.shareit.request;

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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
public class RequestControllerTests {
    @MockBean
    private RequestService mockRequestService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    RequestDto requestDto1 = RequestDto.builder()
            .requestId(1L)
            .requestDescription("request1 description")
            .requestDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .userIdHeader(1L)
            .build();

    RequestDto requestDto2 = RequestDto.builder()
            .requestId(2L)
            .requestDescription("request2 description")
            .requestDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .userIdHeader(1L)
            .build();

    @Test
    @SneakyThrows
    void createRequestControllerTests() {
        when(mockRequestService.createEntityService(any(), any()))
                .thenReturn(requestDto1);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(requestDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto1.getRequestId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto1.getRequestDescription())))
                .andExpect(jsonPath("$.created", is(requestDto1.getRequestDate().toString())));
    }

    @Test
    @SneakyThrows
    void getRequestByIdControllerTests() {
        when(mockRequestService.getEntityService(any(), any(), any()))
                .thenReturn(requestDto2);

        mockMvc.perform(get("/requests/2")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto2.getRequestId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto2.getRequestDescription())))
                .andExpect(jsonPath("$.created", is(requestDto2.getRequestDate().toString())));
    }

    @Test
    @SneakyThrows
    void getRequestsControllerTests() {
        when(mockRequestService.getEntityService(any(), any(), any()))
                .thenReturn(List.of(requestDto2, requestDto1));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(requestDto2.getRequestId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDto2.getRequestDescription())))
                .andExpect(jsonPath("$[0].created", is(requestDto2.getRequestDate().toString())))
                .andExpect(jsonPath("$[1].id", is(requestDto1.getRequestId()), Long.class))
                .andExpect(jsonPath("$[1].description", is(requestDto1.getRequestDescription())))
                .andExpect(jsonPath("$[1].created", is(requestDto1.getRequestDate().toString())));
    }

    @Test
    @SneakyThrows
    void getAllRequestsControllerTests() {
        when(mockRequestService.getEntityService(any(), any(), any()))
                .thenReturn(List.of(requestDto2, requestDto1));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(requestDto2.getRequestId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDto2.getRequestDescription())))
                .andExpect(jsonPath("$[0].created", is(requestDto2.getRequestDate().toString())))
                .andExpect(jsonPath("$[1].id", is(requestDto1.getRequestId()), Long.class))
                .andExpect(jsonPath("$[1].description", is(requestDto1.getRequestDescription())))
                .andExpect(jsonPath("$[1].created", is(requestDto1.getRequestDate().toString())));
    }

    @Test
    @SneakyThrows
    void updateRequestControllerTests() {
        mockMvc.perform(patch("/requests/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(requestDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void deleteRequestByIdControllerTests() {
        mockMvc.perform(delete("/requests/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(mockRequestService, Mockito.times(1)).deleteEntityService(1L);
    }

    @Test
    @SneakyThrows
    void deleteAllRequestControllerTests() {
        mockMvc.perform(delete("/requests/")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(mockRequestService, Mockito.times(1)).deleteEntityService(null);
    }
}
