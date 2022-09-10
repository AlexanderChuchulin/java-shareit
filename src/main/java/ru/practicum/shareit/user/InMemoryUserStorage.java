package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.abstraction.InMemoryStorage;
import ru.practicum.shareit.other.EntityValidator;

@Component
public class InMemoryUserStorage extends InMemoryStorage<User> {

    @Override
    public void validateEntity(User user, Boolean isUpdate, String conclusion) {
        EntityValidator.validateUser(user, conclusion);
    }
}
