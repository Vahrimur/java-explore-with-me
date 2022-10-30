package ru.practicum.event.dto;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.user.dto.UserShortDto;

import java.util.ArrayList;
import java.util.List;

public class EventFullDtoMapper {
    public static EventFullDto mapToEventFullDto(Event event) {
        return new EventFullDto(
                event.getId(),
                event.getAnnotation(),
                new CategoryDto(event.getCategory().getId(), event.getCategory().getName()),
                event.getConfirmedRequests(),
                event.getCreatedOn(),
                event.getDescription(),
                event.getEventDate(),
                new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()),
                new Location(event.getLat(), event.getLon()),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.getRequestModeration(),
                event.getState(),
                event.getTitle(),
                event.getViews()
        );
    }

    public static List<EventFullDto> mapToEventFullDto(Iterable<Event> events) {
        List<EventFullDto> eventFullDtos = new ArrayList<>();
        for (Event event : events) {
            eventFullDtos.add(mapToEventFullDto(event));
        }
        return eventFullDtos;
    }
}
