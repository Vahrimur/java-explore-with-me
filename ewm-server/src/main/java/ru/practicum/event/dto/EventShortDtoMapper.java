package ru.practicum.event.dto;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.model.Event;
import ru.practicum.user.dto.UserShortDto;

import java.util.ArrayList;
import java.util.List;

public class EventShortDtoMapper {
    public static EventShortDto mapToEventShortDto(Event event) {
        return new EventShortDto(
                event.getId(),
                event.getAnnotation(),
                new CategoryDto(event.getCategory().getId(), event.getCategory().getName()),
                event.getConfirmedRequests(),
                event.getEventDate(),
                new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()),
                event.getPaid(),
                event.getTitle(),
                event.getViews()
        );
    }

    public static List<EventShortDto> mapToEventShortDto(Iterable<Event> events) {
        List<EventShortDto> eventShortDtos = new ArrayList<>();
        for (Event event : events) {
            eventShortDtos.add(mapToEventShortDto(event));
        }
        return eventShortDtos;
    }
}
