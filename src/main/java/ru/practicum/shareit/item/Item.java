package ru.practicum.shareit.item;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.abstraction.Entity;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Item extends Entity {
    private String itemName;
    private String itemDesc;
    private Boolean isItemAvailable;
    private User itemOwner;
    private ItemRequest itemRequest;
}
