package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.abstraction.CommonController;

@RestController
@RequestMapping("/users")
public class UserController extends CommonController<UserDto> {

    @Autowired
    public UserController(UserService userService) {
        shareItService = userService;
    }
}


