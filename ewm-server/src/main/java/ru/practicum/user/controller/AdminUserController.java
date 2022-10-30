package ru.practicum.user.controller;

import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exception.IncorrectFieldException;
import ru.practicum.exception.IncorrectObjectException;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "admin/users")
public class AdminUserController {
    private final UserService userService;

    @PostMapping
    public UserDto addUser(@RequestBody NewUserRequest newUserRequest) throws IncorrectFieldException {
        UserDto userDto = userService.createUser(newUserRequest);
        log.info("POST /admin/users {}", userDto);
        return userDto;
    }

    @GetMapping
    public List<UserDto> findUsers(@RequestParam(required = false) List<Long> ids,
                                   @RequestParam(defaultValue = "0") Integer from,
                                   @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /users?ids={}&from={}&size={}", ids, from, size);
        return userService.getUsers(ids, from, size);
    }

    @DeleteMapping(value = "/{userId}")
    public void removeUserById(@PathVariable @NotNull Long userId) throws IncorrectObjectException {
        log.info("DELETE /users/{}", userId);
        userService.deleteUserById(userId);
    }
}
