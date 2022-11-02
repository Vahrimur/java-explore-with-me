package ru.practicum.user.dto;

import ru.practicum.user.model.User;

public class UserShortDtoMapper {
    public static UserShortDto mapToUserShortDto(User user) {
        return new UserShortDto(
                user.getId(),
                user.getName()
        );
    }
}
