package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentNonAuthDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateCommentDto;
import ru.practicum.exception.IncorrectFieldException;
import ru.practicum.exception.IncorrectObjectException;

import java.util.List;

public interface CommentService {
    CommentDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto)
            throws IncorrectObjectException, IncorrectFieldException;

    CommentDto updateComment(Long userId, UpdateCommentDto updateCommentDto)
            throws IncorrectObjectException, IncorrectFieldException;

    List<CommentNonAuthDto> getComments(List<Long> eventIds,
                                        String text,
                                        String rangeStart,
                                        String rangeEnd,
                                        Integer from,
                                        Integer size) throws IncorrectObjectException;

    CommentNonAuthDto getCommentById(Long commentId) throws IncorrectObjectException;

    CommentDto updateCommentByAdmin(UpdateCommentDto updateCommentDto)
            throws IncorrectObjectException, IncorrectFieldException;

    void deleteCommentById(Long commentId) throws IncorrectObjectException;
}
