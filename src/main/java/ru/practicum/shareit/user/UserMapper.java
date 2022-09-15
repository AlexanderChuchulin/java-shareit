package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.abstraction.Mapper;

@Service
public class UserMapper implements Mapper<User, UserDto> {

    @Override
    public UserDto entityToDto(User user) {
        return UserDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .userName(user.getUserName())
                .build();
    }

    @Override
    public User dtoToEntity(UserDto userDto, Long userIdHeader) {
        return User.builder()
                .email(userDto.getEmail())
                .userName(userDto.getUserName())
                .build();
    }
}
