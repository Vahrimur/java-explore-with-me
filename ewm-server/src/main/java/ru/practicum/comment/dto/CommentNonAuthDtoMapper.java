package ru.practicum.comment.dto;

import ru.practicum.comment.model.Comment;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.user.dto.UserShortDto;

public class CommentNonAuthDtoMapper {
    public static CommentNonAuthDto mapToCommentNonAuthDto(Comment comment,
                                                           UserShortDto userShortDto,
                                                           EventShortDto eventShortDto) {
        return new CommentNonAuthDto(
                comment.getId(),
                userShortDto,
                eventShortDto,
                comment.getText(),
                comment.getCreatedOn()
        );
    }
}
