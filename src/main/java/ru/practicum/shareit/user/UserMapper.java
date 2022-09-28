package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    public UserDto userToDto(User user) {
        return UserDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .userName(user.getUserName())
                .build();
    }

    public User dtoToUser(UserDto userDto) {
        return User.builder()
                .email(userDto.getEmail())
                .userName(userDto.getUserName())
                .build();
    }
}
