package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.abstraction.InMemoryStorage;

@Component
public class InMemoryUserStorage extends InMemoryStorage<User, UserDto> {
}
