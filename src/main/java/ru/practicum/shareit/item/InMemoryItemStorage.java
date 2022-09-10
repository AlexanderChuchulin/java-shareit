package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.abstraction.InMemoryStorage;
import ru.practicum.shareit.other.EntityValidator;

@Component
public class InMemoryItemStorage extends InMemoryStorage<Item> {
    private final EntityValidator entityValidator;

    @Autowired
    public InMemoryItemStorage(EntityValidator entityValidator) {
        this.entityValidator = entityValidator;
    }

    @Override
    public void validateEntity(Item item, Boolean isUpdate, String conclusion) {
        entityValidator.validateItem(item, isUpdate, conclusion);
    }
}
