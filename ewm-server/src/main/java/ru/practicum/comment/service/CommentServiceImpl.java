package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.comment.dto.*;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventShortDtoMapper;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.IncorrectFieldException;
import ru.practicum.exception.IncorrectObjectException;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.dto.UserShortDtoMapper;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final EventService eventService;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public CommentDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto)
            throws IncorrectObjectException, IncorrectFieldException {
        userService.checkUserExists(userId);
        eventService.checkEventExists(eventId);
        checkCorrectCommentText(newCommentDto.getText());
        checkSuchCommentExists(userId, eventId, newCommentDto);

        Comment comment = new Comment();
        comment.setAuthor(userRepository.getById(userId));
        comment.setEvent(eventRepository.getById(eventId));
        comment.setText(newCommentDto.getText());
        comment.setCreatedOn(LocalDateTime.now());

        return CommentDtoMapper.mapToCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto updateComment(Long userId, UpdateCommentDto updateCommentDto)
            throws IncorrectObjectException, IncorrectFieldException {
        userService.checkUserExists(userId);
        Long commentId = updateCommentDto.getId();
        checkCommentExists(commentId);
        String newText = updateCommentDto.getText();
        checkCorrectCommentText(newText);
        checkSameCommentText(commentId, newText);

        Comment comment = commentRepository.getById(commentId);
        isCommentDateBeforeOneHour(comment.getCreatedOn());

        comment.setText(newText);

        return CommentDtoMapper.mapToCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentNonAuthDto> getComments(List<Long> eventIds,
                                               String text,
                                               String rangeStart,
                                               String rangeEnd,
                                               Integer from,
                                               Integer size) {
        checkCorrectParams(from, size);
        LocalDateTime startTime;
        LocalDateTime endTime;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (rangeStart == null) {
            startTime = LocalDateTime.now().minusYears(100);
        } else {
            startTime = LocalDateTime.parse(rangeStart, formatter);
        }
        if (rangeStart == null) {
            endTime = LocalDateTime.now().plusYears(100);
        } else {
            endTime = LocalDateTime.parse(rangeEnd, formatter);
        }

        List<Comment> comments;
        if (eventIds == null) {
            comments = commentRepository.findCommentsByParamsWithoutEvents(text, startTime, endTime, from, size);
        } else {
            comments = commentRepository.findCommentsByParamsWithEvents(eventIds, text, startTime, endTime, from, size);
        }

        List<CommentNonAuthDto> commentNonAuthDtos = new ArrayList<>();
        for (Comment comment : comments) {
            UserShortDto userShortDto = UserShortDtoMapper.mapToUserShortDto(comment.getAuthor());
            EventShortDto eventShortDto = EventShortDtoMapper.mapToEventShortDto(comment.getEvent());
            CommentNonAuthDto commentNonAuthDto = CommentNonAuthDtoMapper.mapToCommentNonAuthDto(
                    comment,
                    userShortDto,
                    eventShortDto
            );
            commentNonAuthDtos.add(commentNonAuthDto);
        }

        return commentNonAuthDtos;
    }

    @Override
    public CommentNonAuthDto getCommentById(Long commentId) throws IncorrectObjectException {
        checkCommentExists(commentId);

        Comment comment = commentRepository.getById(commentId);
        UserShortDto userShortDto = UserShortDtoMapper.mapToUserShortDto(comment.getAuthor());
        EventShortDto eventShortDto = EventShortDtoMapper.mapToEventShortDto(comment.getEvent());

        return CommentNonAuthDtoMapper.mapToCommentNonAuthDto(
                comment,
                userShortDto,
                eventShortDto
        );
    }

    @Override
    public CommentDto updateCommentByAdmin(UpdateCommentDto updateCommentDto)
            throws IncorrectObjectException, IncorrectFieldException {
        Long commentId = updateCommentDto.getId();
        checkCommentExists(commentId);
        String newText = updateCommentDto.getText();
        checkCorrectCommentText(newText);
        checkSameCommentText(commentId, newText);

        Comment comment = commentRepository.getById(commentId);
        comment.setText(newText);

        return CommentDtoMapper.mapToCommentDto(commentRepository.save(comment));
    }

    @Override
    public void deleteCommentById(Long commentId) throws IncorrectObjectException {
        checkCommentExists(commentId);

        commentRepository.deleteById(commentId);
    }

    private void checkCommentExists(Long commentId) throws IncorrectObjectException {
        if (!commentRepository.findAll().isEmpty()) {
            List<Long> ids = commentRepository.findAll()
                    .stream()
                    .map(Comment::getId)
                    .collect(Collectors.toList());
            if (!ids.contains(commentId)) {
                throw new IncorrectObjectException("There is no comment with id = " + commentId);
            }
        } else {
            throw new IncorrectObjectException("There is no comment with id = " + commentId);
        }
    }

    private void checkSuchCommentExists(Long userId, Long eventId, NewCommentDto newCommentDto) {
        if (commentRepository.existsByAuthorIdAndEventIdAndText(userId, eventId, newCommentDto.getText())) {
            throw new IllegalArgumentException(
                    "User id = " + userId + " already posted this comment to event id = " + eventId
            );
        }
    }

    private void checkCorrectCommentText(String text) throws IncorrectFieldException {
        if (text.isBlank()) {
            throw new IncorrectFieldException("Text field cannot be blank");
        }
    }

    private void checkSameCommentText(Long commentId, String text) throws IncorrectFieldException {
        if (text.equals(commentRepository.getById(commentId).getText())) {
            throw new IncorrectFieldException("It is impossible to update text of the comment to the same");
        }
    }

    private void isCommentDateBeforeOneHour(LocalDateTime createdOn) throws IncorrectFieldException {
        if (createdOn.isBefore(LocalDateTime.now().minusHours(1))) {
            throw new IncorrectFieldException("It is impossible to change comment after one hour it was created");
        }
    }

    private void checkCorrectParams(Integer from, Integer size) {
        if (from < 0) {
            throw new IllegalArgumentException("From parameter cannot be less zero");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Size parameter cannot be less or equal zero");
        }
    }
}
