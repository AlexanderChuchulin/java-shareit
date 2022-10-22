package ru.practicum.shareit.item;

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
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTests {
    @MockBean
    private ItemService mockItemService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    private final ItemDto itemDto1 = ItemDto.builder()
            .itemId(1L)
            .itemName("Item1Dto1")
            .itemDescription("Item1Dto1 description")
            .isItemAvailable(true)
            .build();

    private final ItemDto itemDto2 = ItemDto.builder()
            .itemId(2L)
            .itemName("Item1Dto2")
            .itemDescription("Item1Dto2 description")
            .isItemAvailable(true)
            .build();

    private final CommentDto commentDto1 = CommentDto.builder()
            .commentId(1L)
            .commentText("commentDto1 text")
            .commentDate(LocalDateTime.now())
            .authorName("commentDto1Author")
            .itemId(1L)
            .userIdHeader(1L)
            .build();

    @Test
    @SneakyThrows
    void createItemControllerTests() {
        when(mockItemService.createEntityService(any(), any()))
                .thenReturn(itemDto1);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto1.getItemId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto1.getItemName())))
                .andExpect(jsonPath("$.description", is(itemDto1.getItemDescription())))
                .andExpect(jsonPath("$.available", is(itemDto1.getIsItemAvailable())));
    }

    @Test
    @SneakyThrows
    void createCommentControllerTests() {
        when(mockItemService.createCommentService(any(), any(), any()))
                .thenReturn(commentDto1);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(commentDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto1.getCommentId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto1.getCommentText())))
                .andExpect(jsonPath("$.created", is(notNullValue())))
                .andExpect(jsonPath("$.authorName", is(commentDto1.getAuthorName())))
                .andExpect(jsonPath("$.itemId", is(commentDto1.getCommentId()), Long.class))
                .andExpect(jsonPath("$.userIdHeader", is(commentDto1.getUserIdHeader()), Long.class));
    }

    @Test
    @SneakyThrows
    void getItemByIdControllerTests() {
        when(mockItemService.getEntityService(any(), any(), any()))
                .thenReturn(itemDto2);

        mockMvc.perform(get("/items/2")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto2.getItemId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto2.getItemName())))
                .andExpect(jsonPath("$.description", is(itemDto2.getItemDescription())))
                .andExpect(jsonPath("$.available", is(itemDto2.getIsItemAvailable())));
    }

    @Test
    @SneakyThrows
    void getItemsControllerTests() {
        when(mockItemService.getEntityService(any(), any(), any()))
                .thenReturn(List.of(itemDto1, itemDto2));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto1.getItemId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto1.getItemName())))
                .andExpect(jsonPath("$[0].description", is(itemDto1.getItemDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto1.getIsItemAvailable())))
                .andExpect(jsonPath("$[1].id", is(itemDto2.getItemId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(itemDto2.getItemName())))
                .andExpect(jsonPath("$[1].description", is(itemDto2.getItemDescription())))
                .andExpect(jsonPath("$[1].available", is(itemDto2.getIsItemAvailable())));
    }

    @Test
    @SneakyThrows
    void getItemsBySearchTextControllerTests() {
        when(mockItemService.getItemBySearchText(any(), any()))
                .thenReturn(List.of(itemDto1, itemDto2));

        mockMvc.perform(get("/items/search?text=test")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto1.getItemId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto1.getItemName())))
                .andExpect(jsonPath("$[0].description", is(itemDto1.getItemDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto1.getIsItemAvailable())))
                .andExpect(jsonPath("$[1].id", is(itemDto2.getItemId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(itemDto2.getItemName())))
                .andExpect(jsonPath("$[1].description", is(itemDto2.getItemDescription())))
                .andExpect(jsonPath("$[1].available", is(itemDto2.getIsItemAvailable())));
    }

    @Test
    @SneakyThrows
    void updateItemControllerTests() {
        ItemDto itemDtoUpdate1 = ItemDto.builder()
                .itemId(1L)
                .itemName("Item1DtoUpdate1")
                .itemDescription("Item1DtoUpdate1 description")
                .isItemAvailable(false)
                .build();

        when(mockItemService.updateEntityService(any(), any(), any()))
                .thenReturn(itemDtoUpdate1);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemDtoUpdate1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoUpdate1.getItemId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoUpdate1.getItemName())))
                .andExpect(jsonPath("$.description", is(itemDtoUpdate1.getItemDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoUpdate1.getIsItemAvailable())));
    }

    @Test
    @SneakyThrows
    void deleteUserByIdControllerTests() {
        mockMvc.perform(delete("/items/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(mockItemService, Mockito.times(1)).deleteEntityService(1L);
    }

    @Test
    @SneakyThrows
    void deleteAllUsersControllerTests() {
        mockMvc.perform(delete("/items/")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(mockItemService, Mockito.times(1)).deleteEntityService(null);
    }
}
