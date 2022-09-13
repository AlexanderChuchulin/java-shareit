package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.abstraction.CommonController;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController extends CommonController<Item, ItemDto> {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
        service = itemService;
    }

    @GetMapping("/search")
    private List<ItemDto> getEntityController(@RequestParam(name = "text") String searchText) {
        return itemService.getItemBySearchText(searchText);
    }
}
