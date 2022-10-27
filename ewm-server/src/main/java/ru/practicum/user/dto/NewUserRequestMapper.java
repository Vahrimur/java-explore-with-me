package ru.practicum.user.dto;

import ru.practicum.user.model.User;

public class NewUserRequestMapper {
    public static User mapToUserEntity(NewUserRequest newUserRequest) {
        return new User(null, newUserRequest.getName(), newUserRequest.getEmail());
    }
}
