package ru.practicum.comment.controller;

import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.UpdateCommentDto;
import ru.practicum.comment.service.CommentService;
import ru.practicum.exception.IncorrectFieldException;
import ru.practicum.exception.IncorrectObjectException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "admin/comments")
public class AdminCommentController {
    private final CommentService commentService;

    @PatchMapping
    public CommentDto updateComment(@RequestBody UpdateCommentDto updateCommentDto)
            throws IncorrectObjectException, IncorrectFieldException {
        log.info("PATCH /admin/comments {}", updateCommentDto);
        return commentService.updateCommentByAdmin(updateCommentDto);
    }

    @DeleteMapping(value = "/{commentId}")
    public void removeCommentById(@PathVariable Long commentId) throws IncorrectObjectException {
        log.info("DELETE /admin/comments/" + commentId);
        commentService.deleteCommentById(commentId);
    }
}
