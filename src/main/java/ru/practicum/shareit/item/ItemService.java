package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.abstraction.CommonMapper;
import ru.practicum.shareit.abstraction.CommonService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService extends CommonService<Item, ItemDto> {
    InMemoryItemStorage inMemoryItemStorage;

    @Autowired
    public ItemService(InMemoryItemStorage inMemoryItemStorage, CommonMapper<Item, ItemDto> commonMapper) {
        this.inMemoryItemStorage = inMemoryItemStorage;
        storage = inMemoryItemStorage;
        mapper = commonMapper;
    }

    List<ItemDto> getItemBySearchText(String searchText) {
        if (searchText.isBlank()) {
            return List.of();
        }
        return inMemoryItemStorage.getEntityMap().values().stream()
                .filter(item -> item.getItemName().concat(item.getItemDesc()).toLowerCase().contains(searchText.toLowerCase()))
                .filter(Item::getIsItemAvailable)
                .map(mapper::entityToDto)
                .collect(Collectors.toList());
    }
}
