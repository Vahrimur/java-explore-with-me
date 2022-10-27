package ru.practicum.event.service;

import ru.practicum.event.dto.*;
import ru.practicum.event.model.EventSort;
import ru.practicum.exception.IncorrectFieldException;
import ru.practicum.exception.IncorrectObjectException;
import ru.practicum.exception.WrongConditionException;

import java.util.List;

public interface EventService {
    EventFullDto createEvent(Long userId, NewEventDto newEventDto)
            throws IncorrectFieldException, IncorrectObjectException;

    EventFullDto updateEvent(Long userId, UpdateEventRequest updateEventRequest)
            throws IncorrectObjectException, IncorrectFieldException, WrongConditionException;

    void checkEventExists(Long eventId) throws IncorrectObjectException;

    EventFullDto cancelEvent(Long userId, Long eventId)
            throws IncorrectObjectException, IncorrectFieldException, WrongConditionException;

    List<EventShortDto> getEventsByInitiator(Long userId, Integer from, Integer size) throws IncorrectObjectException;

    EventFullDto getEventByInitiator(Long userId, Long eventId)
            throws IncorrectObjectException, IncorrectFieldException;

    List<EventShortDto> getEvents(String text,
                                  List<Long> categories,
                                  Boolean paid,
                                  String rangeStart,
                                  String rangeEnd,
                                  Boolean onlyAvailable,
                                  EventSort sort,
                                  Integer from,
                                  Integer size
    );

    EventFullDto getEventById(Long eventId) throws IncorrectObjectException;

    EventFullDto updateEventByAdmin(Long eventId, AdminUpdateEventRequest adminUpdateEventRequest)
            throws IncorrectObjectException;

    EventFullDto publishEventByAdmin(Long eventId) throws IncorrectObjectException, WrongConditionException;

    EventFullDto rejectEventByAdmin(Long eventId) throws IncorrectObjectException, WrongConditionException;

    List<EventFullDto> getEventsByAdmin(List<Long> users,
                                        List<String> states,
                                        List<Long> categories,
                                        String rangeStart,
                                        String rangeEnd,
                                        Integer from,
                                        Integer size
    );

    List<EventShortDto> getEventsByCompilationId(Long compilationId);

    void checkUserNotInitiatorOfEvent(Long eventId, Long userId) throws IncorrectFieldException;

    void checkUserInitiatorOfEvent(Long eventId, Long userId) throws IncorrectFieldException;

    void checkEventStatePublished(Long eventId) throws WrongConditionException;

    void checkEventLimit(Long eventId) throws WrongConditionException;

    void checkNullLimitOrNotPreModeration(Long eventId) throws WrongConditionException;

    void addViewsForEvents(List<EventShortDto> events);

    void addViewForEvent(EventFullDto eventFullDto);
}
