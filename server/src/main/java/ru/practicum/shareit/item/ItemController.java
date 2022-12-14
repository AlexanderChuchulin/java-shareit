package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.abstraction.CommonController;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController extends CommonController<ItemDto> {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
        shareItService = itemService;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createCommentController(@PathVariable Long itemId, @RequestBody CommentDto commentDto,
                                      @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userIdHeader) {
        return itemService.createCommentService(commentDto, itemId, userIdHeader);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemBySearchTextController(@RequestParam(value = "from", required = false, defaultValue = "0") String from,
                                                        @RequestParam(value = "size", required = false) String size,
                                                        @RequestParam(name = "text") String searchText) {
        return itemService.getItemBySearchText(searchText, from, size, searchText);
    }


}
