package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.AdminUpdateEventRequest;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.IncorrectObjectException;
import ru.practicum.exception.WrongConditionException;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "admin/events")
public class AdminEventController {
    private final EventService eventService;

    @PutMapping(value = "/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long eventId,
                                    @RequestBody AdminUpdateEventRequest adminUpdateEventRequest)
            throws IncorrectObjectException {
        log.info("PUT /admin/events/{} {}", eventId, adminUpdateEventRequest);
        return eventService.updateEventByAdmin(eventId, adminUpdateEventRequest);
    }

    @PatchMapping(value = "/{eventId}/publish")
    public EventFullDto publishEvent(@PathVariable Long eventId)
            throws IncorrectObjectException, WrongConditionException {
        log.info("PATCH /admin/events/{}/publish", eventId);
        return eventService.publishEventByAdmin(eventId);
    }

    @PatchMapping(value = "/{eventId}/reject")
    public EventFullDto rejectEvent(@PathVariable Long eventId)
            throws WrongConditionException, IncorrectObjectException {
        log.info("PATCH /admin/events/{}/reject", eventId);
        return eventService.rejectEventByAdmin(eventId);
    }

    @GetMapping
    public List<EventFullDto> findEvents(@RequestParam(required = false) List<Long> users,
                                         @RequestParam(required = false) List<String> states,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) String rangeStart,
                                         @RequestParam(required = false) String rangeEnd,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /admin/events?users={}&states={}&categories={}&rangeStart={}&rangeEnd={}&from={}&size={}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        return eventService.getEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }
}
