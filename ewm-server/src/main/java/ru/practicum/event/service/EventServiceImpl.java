package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.category.service.CategoryService;
import ru.practicum.compilation.repository.CompilationEventRepository;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventSort;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.IncorrectFieldException;
import ru.practicum.exception.IncorrectObjectException;
import ru.practicum.exception.WrongConditionException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final CompilationEventRepository compilationEventRepository;

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto)
            throws IncorrectFieldException, IncorrectObjectException {
        isEventDateAfterTwoHours(newEventDto.getEventDate());
        userService.checkUserExists(userId);
        Long catId = newEventDto.getCategory();
        categoryService.checkCategoryExist(catId);

        Event event = NewEventDtoMapper.mapToEventEntity(newEventDto);
        User initiator = userRepository.getById(userId);
        Category category = categoryRepository.getById(catId);
        event.setInitiator(initiator);
        event.setCategory(category);
        event.setState(EventState.PENDING);
        return EventFullDtoMapper.mapToEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto updateEvent(Long userId, UpdateEventRequest updateEventRequest)
            throws IncorrectObjectException, IncorrectFieldException, WrongConditionException {
        Long eventId = updateEventRequest.getEventId();
        userService.checkUserExists(userId);
        checkEventExists(eventId);
        checkNotEventInitiator(eventId, userId);
        Long catId = updateEventRequest.getCategory();
        categoryService.checkCategoryExist(catId);
        isEventStateNotPublished(eventId);
        isEventDateAfterTwoHours(updateEventRequest.getEventDate());

        Event event = eventRepository.getById(eventId);
        event.setAnnotation(updateEventRequest.getAnnotation());
        event.setCategory(categoryRepository.getById(catId));
        event.setDescription(updateEventRequest.getDescription());
        event.setEventDate(updateEventRequest.getEventDate());
        event.setPaid(updateEventRequest.getPaid());
        event.setParticipantLimit(updateEventRequest.getParticipantLimit());
        event.setTitle(updateEventRequest.getTitle());

        if (event.getState().equals(EventState.CANCELED)) {
            event.setState(EventState.PENDING);
        }

        return EventFullDtoMapper.mapToEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto cancelEvent(Long userId, Long eventId)
            throws IncorrectObjectException, IncorrectFieldException, WrongConditionException {
        userService.checkUserExists(userId);
        checkEventExists(eventId);
        checkNotEventInitiator(eventId, userId);
        isEventStatePending(eventId);

        Event event = eventRepository.getById(eventId);
        event.setState(EventState.CANCELED);

        return EventFullDtoMapper.mapToEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getEventsByInitiator(Long userId, Integer from, Integer size)
            throws IncorrectObjectException {
        userService.checkUserExists(userId);
        checkCorrectParams(from, size);

        return EventShortDtoMapper.mapToEventShortDto(eventRepository.findAllByParams(userId, from, size));
    }

    @Override
    public EventFullDto getEventByInitiator(Long userId, Long eventId)
            throws IncorrectObjectException, IncorrectFieldException {
        userService.checkUserExists(userId);
        checkEventExists(eventId);
        checkNotEventInitiator(eventId, userId);

        return EventFullDtoMapper.mapToEventFullDto(eventRepository.findAllByInitiatorIdAndId(userId, eventId));
    }

    @Override
    public List<EventShortDto> getEvents(String text,
                                         List<Long> categories,
                                         Boolean paid,
                                         String rangeStart,
                                         String rangeEnd,
                                         Boolean onlyAvailable,
                                         EventSort sort,
                                         Integer from,
                                         Integer size) {
        checkCorrectParams(from, size);
        LocalDateTime startTime;
        LocalDateTime endTime;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (rangeStart == null) {
            startTime = LocalDateTime.now();
        } else {
            startTime = LocalDateTime.parse(rangeStart, formatter);
        }
        if (rangeStart == null) {
            endTime = LocalDateTime.now().plusYears(100);
        } else {
            endTime = LocalDateTime.parse(rangeEnd, formatter);
        }

        List<Event> events;
        if (categories == null) {
            if (onlyAvailable) {
                events = eventRepository.findEventsByManyParamsWithoutCatAvailable(
                        text, paid, startTime, endTime, from, size);
            } else {
                events = eventRepository.findEventsByManyParamsWithoutCat(
                        text, paid, startTime, endTime, from, size);
            }
        } else {
            if (onlyAvailable) {
                events = eventRepository.findEventsByManyParamsWithCatAvailable(
                        text, categories, paid, startTime, endTime, from, size);
            } else {
                events = eventRepository.findEventsByManyParamsWithCat(
                        text, categories, paid, startTime, endTime, from, size);
            }
        }
        if (sort != null) {
            if (sort == EventSort.EVENT_DATE) {
                events = events.stream().sorted(Comparator.comparing(Event::getEventDate)).collect(Collectors.toList());
            }
            if (sort == EventSort.VIEWS) {
                events = events.stream().sorted(Comparator.comparing(Event::getViews)).collect(Collectors.toList());
            }
        }

        return EventShortDtoMapper.mapToEventShortDto(events);
    }

    @Override
    public EventFullDto getEventById(Long eventId) throws IncorrectObjectException {
        checkEventExists(eventId);

        return EventFullDtoMapper.mapToEventFullDto(eventRepository.getByIdPublished(eventId));
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, AdminUpdateEventRequest adminUpdateEventRequest)
            throws IncorrectObjectException {
        checkEventExists(eventId);

        Event event = eventRepository.getById(eventId);
        Category category = categoryRepository.getById(adminUpdateEventRequest.getCategory());
        if (adminUpdateEventRequest.getAnnotation() != null) {
            event.setAnnotation(adminUpdateEventRequest.getAnnotation());
        }
        if (adminUpdateEventRequest.getCategory() != null) {
            event.setCategory(category);
        }
        if (adminUpdateEventRequest.getDescription() != null) {
            event.setDescription(adminUpdateEventRequest.getDescription());
        }
        if (adminUpdateEventRequest.getEventDate() != null) {
            event.setEventDate(adminUpdateEventRequest.getEventDate());
        }
        if (adminUpdateEventRequest.getLocation() != null) {
            event.setLon(adminUpdateEventRequest.getLocation().getLon());
            event.setLat(adminUpdateEventRequest.getLocation().getLat());
        }
        if (adminUpdateEventRequest.getPaid() != null) {
            event.setPaid(adminUpdateEventRequest.getPaid());
        }
        if (adminUpdateEventRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(adminUpdateEventRequest.getParticipantLimit());
        }
        if (adminUpdateEventRequest.getRequestModeration() != null) {
            event.setRequestModeration(adminUpdateEventRequest.getRequestModeration());
        }
        if (adminUpdateEventRequest.getTitle() != null) {
            event.setTitle(adminUpdateEventRequest.getTitle());
        }
        event.setState(EventState.PENDING);

        return EventFullDtoMapper.mapToEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto publishEventByAdmin(Long eventId) throws IncorrectObjectException, WrongConditionException {
        checkEventExists(eventId);

        Event event = eventRepository.getById(eventId);
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new WrongConditionException("Start time of event must be at least 1 hour from now");
        }
        if (!event.getState().equals(EventState.PENDING)) {
            throw new WrongConditionException("Event must be in state PENDING to be published");
        }
        event.setState(EventState.PUBLISHED);
        return EventFullDtoMapper.mapToEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto rejectEventByAdmin(Long eventId) throws IncorrectObjectException, WrongConditionException {
        checkEventExists(eventId);

        Event event = eventRepository.getById(eventId);
        if (!event.getState().equals(EventState.PENDING)) {
            throw new WrongConditionException("Event must not be in state PENDING to be rejected");
        }
        event.setState(EventState.CANCELED);
        return EventFullDtoMapper.mapToEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users,
                                               List<String> states,
                                               List<Long> categories,
                                               String rangeStart,
                                               String rangeEnd,
                                               Integer from,
                                               Integer size) {
        checkCorrectParams(from, size);
        LocalDateTime startTime;
        LocalDateTime endTime;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (rangeStart == null) {
            startTime = LocalDateTime.now();
        } else {
            startTime = LocalDateTime.parse(rangeStart, formatter);
        }
        if (rangeStart == null) {
            endTime = LocalDateTime.now().plusYears(100);
        } else {
            endTime = LocalDateTime.parse(rangeEnd, formatter);
        }

        List<EventState> eventStates = null;
        if (states != null) {
            eventStates = states.stream().map(EventState::valueOf).collect(Collectors.toList());
        }

        List<Event> events = eventRepository.findEventsByManyParamsByAdmin(
                users, eventStates, categories, startTime, endTime);
        if (events.size() < size) {
            size = events.size();
        }

        return EventFullDtoMapper.mapToEventFullDto(events.subList(from, from + size));
    }

    @Override
    public List<EventShortDto> getEventsByCompilationId(Long compilationId) {
        List<Long> eventsIds = compilationEventRepository.getCompilationEventIds(compilationId);
        return EventShortDtoMapper.mapToEventShortDto(eventRepository.getAllByIds(eventsIds));
    }

    private void isEventDateAfterTwoHours(LocalDateTime date) throws IncorrectFieldException {
        if (date.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IncorrectFieldException("Time of event must be after two hours from now");
        }
    }

    @Override
    public void checkEventExists(Long eventId) throws IncorrectObjectException {
        if (!eventRepository.findAll().isEmpty()) {
            List<Long> ids = eventRepository.findAll()
                    .stream()
                    .map(Event::getId)
                    .collect(Collectors.toList());
            if (!ids.contains(eventId)) {
                throw new IncorrectObjectException("There is no event with id = " + eventId);
            }
        } else {
            throw new IncorrectObjectException("There is no event with id = " + eventId);
        }
    }

    private void checkNotEventInitiator(Long eventId, Long userId) throws IncorrectFieldException {
        if (!Objects.equals(eventRepository.getById(eventId).getInitiator().getId(), userId)) {
            throw new IncorrectFieldException("User id = " + userId + " is not initiator of event id = " + eventId);
        }
    }

    @Override
    public void checkUserNotInitiatorOfEvent(Long eventId, Long userId) throws IncorrectFieldException {
        if (Objects.equals(eventRepository.getById(eventId).getInitiator().getId(), userId)) {
            throw new IncorrectFieldException("User id = " + userId + " is the initiator of event id = " + eventId);
        }
    }

    @Override
    public void checkUserInitiatorOfEvent(Long eventId, Long userId) throws IncorrectFieldException {
        if (!Objects.equals(eventRepository.getById(eventId).getInitiator().getId(), userId)) {
            throw new IncorrectFieldException("User id = " + userId + " is not the initiator of event id = " + eventId);
        }
    }

    private void isEventStateNotPublished(Long eventId) throws WrongConditionException {
        Event event = eventRepository.getById(eventId);
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new WrongConditionException("Event in state PUBLISHED cannot be changed");
        }
    }

    @Override
    public void checkEventStatePublished(Long eventId) throws WrongConditionException {
        Event event = eventRepository.getById(eventId);
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new WrongConditionException("It is impossible to create a request to not PUBLISHED event");
        }
    }

    private void isEventStatePending(Long eventId) throws WrongConditionException {
        Event event = eventRepository.getById(eventId);
        EventState state = event.getState();
        if (!state.equals(EventState.PENDING)) {
            throw new WrongConditionException("Event in state " + state + " cannot be changed");
        }
    }

    @Override
    public void checkEventLimit(Long eventId) throws WrongConditionException {
        Event event = eventRepository.getById(eventId);
        int spareParticipantSlots = event.getParticipantLimit() - event.getConfirmedRequests();
        if (spareParticipantSlots == 0) {
            throw new WrongConditionException("Participants limit for this event has been reached");
        }
    }

    @Override
    public void checkNullLimitOrNotPreModeration(Long eventId) throws WrongConditionException {
        Event event = eventRepository.getById(eventId);
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            throw new WrongConditionException("No CONFIRM for this request is needed");
        }
    }

    @Override
    public void addViewsForEvents(List<EventShortDto> eventsShortDtos) {
        for (EventShortDto eventShortDto : eventsShortDtos) {
            Event event = eventRepository.getById(eventShortDto.getId());
            event.setViews(event.getViews() + 1);
            eventRepository.save(event);
        }
    }

    @Override
    public void addViewForEvent(EventFullDto eventFullDto) {
        Event event = eventRepository.getById(eventFullDto.getId());
        event.setViews(event.getViews() + 1);
        eventRepository.save(event);
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
