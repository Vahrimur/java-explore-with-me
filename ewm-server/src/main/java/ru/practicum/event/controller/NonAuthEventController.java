package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.EventSort;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.IncorrectObjectException;
import ru.practicum.stats.client.StatClient;


import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "events")
public class NonAuthEventController {
    private final EventService eventService;
    private final StatClient statClient;

    @GetMapping
    public List<EventShortDto> findEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) EventSort sort,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest httpServletRequest) throws IOException {
        statClient.sendStats(httpServletRequest);
        List<EventShortDto> eventsShortDtos = eventService.getEvents(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size
        );
        eventService.addViewsForEvents(eventsShortDtos);
        log.info("GET /events?text={}&categories={}&paid={}&rangeStart={}&rangeEnd={}&onlyAvailable={}&sort={}" +
                "&from={}&size={}", text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        return eventService.getEvents(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size
        );
    }

    @GetMapping(value = "/{eventId}")
    public EventFullDto findEventById(@PathVariable(value = "eventId") Long eventId,
                                      HttpServletRequest httpServletRequest) throws IncorrectObjectException, IOException {
        statClient.sendStats(httpServletRequest);
        EventFullDto eventFullDto = eventService.getEventById(eventId);
        eventService.addViewForEvent(eventFullDto);
        log.info("GET /events/{}", eventId);
        return eventService.getEventById(eventId);
    }
}
