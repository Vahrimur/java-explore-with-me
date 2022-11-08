package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateCommentDto;
import ru.practicum.comment.service.CommentService;
import ru.practicum.exception.IncorrectFieldException;
import ru.practicum.exception.IncorrectObjectException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "users")
public class AuthCommentController {
    private final CommentService commentService;

    @PostMapping(value = "/{userId}/events/{eventId}/comments")
    public CommentDto addComment(@RequestBody NewCommentDto newCommentDto,
                                 @PathVariable(value = "userId") Long userId,
                                 @PathVariable(value = "eventId") Long eventId)
            throws IncorrectObjectException, IncorrectFieldException {
        log.info("POST /users/{}/events/{}/comments", userId, eventId);
        return commentService.createComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{userId}/comments")
    public CommentDto updateComment(@RequestBody UpdateCommentDto updateCommentDto,
                                    @PathVariable(value = "userId") Long userId)
            throws IncorrectObjectException, IncorrectFieldException {
        log.info("PATCH /users/{}/comments {}", userId, updateCommentDto);
        return commentService.updateComment(userId, updateCommentDto);
    }
}
