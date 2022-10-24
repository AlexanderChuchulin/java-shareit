package ru.practicum.shareit.user;


import ru.practicum.shareit.ValidationGroup.CreateGroup;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    @NotBlank(groups = CreateGroup.class)
    @Email
    private String email;
    @NotBlank(groups = CreateGroup.class)
    private String name;
}
