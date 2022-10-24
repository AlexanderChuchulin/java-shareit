package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.ValidationGroup.CreateGroup;

import javax.validation.Valid;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;
    private final String entityName = "User";

    @PostMapping
    public ResponseEntity<Object> createUserController(@RequestBody @Validated(CreateGroup.class) UserDto userDto) {
        log.info("Creating {} {}", entityName, userDto);
        return userClient.createUserClient(userDto);
    }

    @GetMapping(value = {"", "/{userId}"})
    public ResponseEntity<Object> getUsersController(@PathVariable(required = false) Long userId) {
        String path = "";

        if (userId == null) {
            log.info("Get {}", entityName + "s");
        } else {
            path = "/" + userId;
            log.info("Get {} by id={}", entityName, userId);
        }
        return userClient.getUsersClient(path);
    }

    @PatchMapping(path = "/{userId}")
    public ResponseEntity<Object> updateUserController(@PathVariable Long userId, @RequestBody @Valid UserDto userDto) {
        log.info("Updating {} {}", entityName, userDto);
        return userClient.updateUserClient("/" + userId, userDto);
    }

    @DeleteMapping(value = {"", "/{userId}"})
    public ResponseEntity<Object> deleteUsersController(@PathVariable(required = false) Long userId) {
        String path = "";

        if (userId == null) {
            log.info("Delete {}", entityName + "s");
        } else {
            path = "/" + userId;
            log.info("Delete {} by id={}", entityName, userId);
        }
        return userClient.deleteUsersClient(path);
    }
}


