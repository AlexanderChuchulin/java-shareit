package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.abstraction.InMemoryStorage;

@Component
public class InMemoryItemStorage extends InMemoryStorage<Item, ItemDto> {
}
