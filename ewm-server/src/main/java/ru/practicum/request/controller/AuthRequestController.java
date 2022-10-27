package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exception.IncorrectFieldException;
import ru.practicum.exception.IncorrectObjectException;
import ru.practicum.exception.WrongConditionException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "users")
public class AuthRequestController {
    private final RequestService requestService;

    //Добавление запроса от текущего пользователя на участие в событии
    @PostMapping(value = "/{userId}/requests")
    public ParticipationRequestDto addRequest(@PathVariable(value = "userId") Long userId,
                                              @RequestParam("eventId") Long eventId)
            throws WrongConditionException, IncorrectObjectException, IncorrectFieldException {
        log.info("POST /users/{}/requests for event id = {}", userId, eventId);
        return requestService.createRequest(userId, eventId);
    }

    //Отмена своего запроса на участие в событии
    @PatchMapping(value = "/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable(value = "userId") Long userId,
                                                 @PathVariable(value = "requestId") Long requestId)
            throws WrongConditionException, IncorrectObjectException, IncorrectFieldException {
        log.info("PATCH /users/{}/requests/{}/cancel", userId, requestId);
        return requestService.cancelRequest(userId, requestId);
    }

    //Получение информации о заявках текущего пользователя на участие в чужих событиях
    @GetMapping(value = "/{userId}/requests")
    public List<ParticipationRequestDto> getRequests(@PathVariable(value = "userId") Long userId)
            throws IncorrectObjectException {
        log.info("GET /users/{}/requests", userId);
        return requestService.getRequests(userId);
    }
}
