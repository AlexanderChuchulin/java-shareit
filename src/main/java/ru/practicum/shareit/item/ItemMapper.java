package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.abstraction.Mapper;
import ru.practicum.shareit.abstraction.Storage;
import ru.practicum.shareit.user.User;

@Service
public class ItemMapper implements Mapper<Item, ItemDto> {
    private final Storage<User> userStorage;

    @Autowired
    public ItemMapper(Storage<User> userStorage) {
        this.userStorage = userStorage;
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
    public Item dtoToEntity(ItemDto itemDto, Long userIdHeader) {
        return Item.builder()
                .itemName(itemDto.getItemName())
                .itemDesc(itemDto.getItemDesc())
                .isItemAvailable(itemDto.getIsItemAvailable())
                .itemOwner(userStorage.getEntityMapStorage().get(userIdHeader))
                .userIdHeader(userIdHeader)
                .build();
    }
}
