package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.abstraction.InMemoryStorage;
import ru.practicum.shareit.other.EntityValidator;


@Component
public class InMemoryUserStorage extends InMemoryStorage<User> {
    private final EntityValidator entityValidator = new EntityValidator(this);

    @Override
    public void validateEntity(User user, Boolean isUpdate, String conclusion) {
        entityValidator.validateUser(user, conclusion);
    }
}
