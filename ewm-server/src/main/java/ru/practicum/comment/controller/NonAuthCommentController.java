package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentNonAuthDto;
import ru.practicum.comment.service.CommentService;
import ru.practicum.exception.IncorrectObjectException;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "comments")
public class NonAuthCommentController {
    private final CommentService commentService;

    @GetMapping
    public List<CommentNonAuthDto> findComments(
            @RequestParam(required = false) List<Long> eventIds,
            @RequestParam(required = false) String text,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) throws IncorrectObjectException {
        log.info("GET /comments?eventId={}&text={}&rangeStart={}&rangeEnd={}&from={}&size={}",
                eventIds, text, rangeStart, rangeEnd, from, size);
        return commentService.getComments(eventIds, text, rangeStart, rangeEnd, from, size);
    }

    @GetMapping(value = "/{commentId}")
    public CommentNonAuthDto findCommentById(@PathVariable(value = "commentId") Long commentId)
            throws IncorrectObjectException {
        log.info("GET /comments/{}", commentId);
        return commentService.getCommentById(commentId);
    }
}
