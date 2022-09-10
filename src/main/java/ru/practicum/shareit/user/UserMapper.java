package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.abstraction.CommonMapper;

@Service
public class UserMapper extends CommonMapper<User, UserDto> {

    @Override
    public UserDto entityToDto(User user) {
        return UserDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .userName(user.getUserName())
                .build();
    }

    @Override
    protected User dtoToEntity(UserDto userDto, Integer userIdHeader) {
        return User.builder()
                .email(userDto.getEmail())
                .userName(userDto.getUserName())
                .build();
    }
}
