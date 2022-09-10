package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.abstraction.InMemoryStorage;
import ru.practicum.shareit.other.EntityValidator;

@Component
public class InMemoryItemStorage extends InMemoryStorage<Item> {

    @Override
    public void validateEntity(Item item, Boolean isUpdate, String conclusion) {
        EntityValidator.validateItem(item, isUpdate, conclusion);
    }
}
