package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.abstraction.CommonMapper;
import ru.practicum.shareit.user.InMemoryUserStorage;

@Service
public class ItemMapper extends CommonMapper<Item, ItemDto> {
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public ItemMapper(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    @Override
    public ItemDto entityToDto(Item item) {
        return ItemDto.builder()
                .itemId(item.getId())
                .itemName(item.getItemName())
                .itemDesc(item.getItemDesc())
                .isItemAvailable(item.getIsItemAvailable())
                .itemOwner(item.getItemOwner())
                .build();
    }

    @Override
    protected Item dtoToEntity(ItemDto itemDto, Integer userIdHeader) {
        return Item.builder()
                .itemName(itemDto.getItemName())
                .itemDesc(itemDto.getItemDesc())
                .isItemAvailable(itemDto.getIsItemAvailable())
                .itemOwner(inMemoryUserStorage.getEntityMap().get(userIdHeader))
                .userIdHeader(userIdHeader)
                .build();
    }
}
