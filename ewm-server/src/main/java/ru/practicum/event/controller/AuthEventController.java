package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventRequest;
import ru.practicum.event.service.EventService;
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
public class AuthEventController {
    private final EventService eventService;
    private final RequestService requestService;

    //Добавление нового события
    @PostMapping(value = "/{userId}/events")
    public EventFullDto addEvent(@PathVariable(value = "userId") Long userId,
                                 @RequestBody NewEventDto newEventDto)
            throws IncorrectFieldException, IncorrectObjectException {
        log.info("POST /users/{}/events {}", userId, newEventDto);
        return eventService.createEvent(userId, newEventDto);
    }

    //Изменение события, добавленного текущим пользователем
    @PatchMapping(value = "/{userId}/events")
    public EventFullDto updateEvent(@PathVariable(value = "userId") Long userId,
                                    @RequestBody UpdateEventRequest updateEventRequest)
            throws WrongConditionException, IncorrectObjectException, IncorrectFieldException {
        log.info("PATCH /users/{}/events {}", userId, updateEventRequest);
        return eventService.updateEvent(userId, updateEventRequest);
    }

    //Отмена события, добавленного текущим пользователем
    @PatchMapping(value = "/{userId}/events/{eventId}")
    public EventFullDto cancelEvent(@PathVariable(value = "userId") Long userId,
                                    @PathVariable(value = "eventId") Long eventId)
            throws WrongConditionException, IncorrectObjectException, IncorrectFieldException {
        log.info("PATCH /users/{}/events/{}", userId, eventId);
        return eventService.cancelEvent(userId, eventId);
    }

    // Получение событий, добавленных текущим пользователем
    @GetMapping(value = "/{userId}/events")
    public List<EventShortDto> findEventsByInitiator(@PathVariable(value = "userId") Long userId,
                                                     @RequestParam(required = false) Integer from,
                                                     @RequestParam(required = false) Integer size)
            throws IncorrectObjectException {
        log.info("GET /users/{}/events?from={}&size={}", userId, from, size);
        return eventService.getEventsByInitiator(userId, from, size);
    }

    // Получение полной информации о событии, добавленном текущим пользователем
    @GetMapping(value = "/{userId}/events/{eventId}")
    public EventFullDto findEventByInitiator(@PathVariable(value = "userId") Long userId,
                                             @PathVariable(value = "eventId") Long eventId)
            throws IncorrectObjectException, IncorrectFieldException {
        log.info("GET /users/{}/events/{}", userId, eventId);
        return eventService.getEventByInitiator(userId, eventId);
    }

    //Подтверждение чужой заявки на участие в событии текущего пользователя
    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto confirmRequestByInitiator(@PathVariable Long userId,
                                                             @PathVariable Long eventId,
                                                             @PathVariable Long reqId)
            throws IncorrectObjectException, WrongConditionException, IncorrectFieldException {
        log.info("PATCH /users/{}/events/{}/requests/{}/confirm", userId, eventId, reqId);
        return requestService.confirmRequestByInitiator(userId, eventId, reqId);
    }

    //Отклонение чужой заявки на участие в событии текущего пользователя
    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto rejectRequestByInitiator(@PathVariable Long userId,
                                                            @PathVariable Long eventId,
                                                            @PathVariable Long reqId)
            throws WrongConditionException, IncorrectObjectException, IncorrectFieldException {
        log.info("PATCH /users/{}/events/{}/requests/{}/reject", userId, eventId, reqId);
        return requestService.rejectRequestByInitiator(userId, eventId, reqId);
    }

    //Получение информации о запросах на участие в событии текущего пользователя
    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestByInitiator(@PathVariable Long userId,
                                                               @PathVariable Long eventId)
            throws IncorrectObjectException, IncorrectFieldException {
        log.info("GET /users/{}/events/{}/requests", userId, eventId);
        return requestService.getRequestsByInitiator(userId, eventId);
    }
}
