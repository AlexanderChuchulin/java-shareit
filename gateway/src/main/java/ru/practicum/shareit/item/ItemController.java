package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.ValidationGroup.CreateGroup;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private final String entityName = "Item";

    @PostMapping
    public ResponseEntity<Object> createItemController(@RequestHeader("X-Sharer-User-Id") long userIdHeader,
                                                       @RequestBody @Validated(CreateGroup.class) ItemDto itemDto) {
        log.info("Creating {} {}", entityName, itemDto);
        return itemClient.createItemClient(userIdHeader, itemDto);
    }

    @PostMapping(path = "/{itemId}/comment")
    public ResponseEntity<Object> createCommentController(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userIdHeader,
                                                          @RequestBody @Validated CommentDto commentDto) {
        log.info("Creating Comment {}", commentDto);
        return itemClient.createCommentClient(itemId, userIdHeader, commentDto);
    }

    @GetMapping(value = {"", "/{itemId}", "/search"})
    public ResponseEntity<Object> getItemsController(@PathVariable(required = false) Long itemId,
                                                     @PositiveOrZero @RequestParam(value = "from", required = false, defaultValue = "0") String from,
                                                     @Positive @RequestParam(value = "size", required = false) String size,
                                                     @RequestParam(name = "text", required = false) String searchText,
                                                     @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userIdHeader) {

        if (itemId == null) {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("from", from);
            parameters.put("size", size);
            parameters.put("text", searchText);

            log.info("Get {}", entityName + "s");
            return itemClient.getItemClient(null, userIdHeader, parameters);
        }
        log.info("Get {} by id={}", entityName, itemId);
        return itemClient.getItemClient(itemId, userIdHeader, null);
    }

    @PatchMapping(path = "/{itemId}")
    public ResponseEntity<Object> updateItemController(@RequestHeader(value = "X-Sharer-User-Id") long userIdHeader,
                                                       @PathVariable long itemId, @RequestBody @Valid ItemDto itemDto) {
        log.info("Updating {} {}", entityName, itemDto);
        return itemClient.updateItemClient("/" + itemId, userIdHeader, itemDto);
    }

    @DeleteMapping(value = {"", "/{itemId}"})
    public ResponseEntity<Object> deleteUsersController(@PathVariable(required = false) Long itemId) {
        String path = "";

        if (itemId == null) {
            log.info("Delete {}", entityName + "s");
        } else {
            log.info("Delete {} by id={}", entityName, itemId);
            path = "/" + itemId;
        }
        return itemClient.deleteItemClient(path);
    }
}


