package ru.practicum.user.dto;

import lombok.Value;

@Value
public class NewUserRequest {
    String name;
    String email;
}
