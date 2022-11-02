package ru.practicum.comment.dto;

import ru.practicum.comment.model.Comment;

public class CommentDtoMapper {
    public static CommentDto mapToCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getAuthor().getId(),
                comment.getEvent().getId(),
                comment.getText(),
                comment.getCreatedOn()
        );
    }
}
