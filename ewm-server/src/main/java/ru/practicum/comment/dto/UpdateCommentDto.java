package ru.practicum.comment.dto;

import lombok.Value;

@Value
public class UpdateCommentDto {
    Long id;
    String text;
}
