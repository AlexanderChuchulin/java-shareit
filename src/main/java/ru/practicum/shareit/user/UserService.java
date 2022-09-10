package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.abstraction.CommonMapper;
import ru.practicum.shareit.abstraction.CommonService;

@Service
public class UserService extends CommonService<User, UserDto> {

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage, CommonMapper<User, UserDto> commonMapper) {
        storage = inMemoryUserStorage;
        mapper = commonMapper;
    }
}
