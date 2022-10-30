package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.IncorrectFieldException;
import ru.practicum.exception.IncorrectObjectException;
import ru.practicum.exception.WrongConditionException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.ParticipationRequestDtoMapper;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final EventService eventService;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId)
            throws IncorrectObjectException, IncorrectFieldException, WrongConditionException {
        userService.checkUserExists(userId);
        eventService.checkEventExists(eventId);
        checkRequestAlreadyExist(userId, eventId);
        eventService.checkUserNotInitiatorOfEvent(eventId, userId);
        eventService.checkEventStatePublished(eventId);
        eventService.checkEventLimit(eventId);

        ParticipationRequest request = new ParticipationRequest();
        request.setRequester(userRepository.getById(userId));
        request.setEvent(eventRepository.getById(eventId));
        request.setCreated(LocalDateTime.now());
        if (eventRepository.getById(eventId).getRequestModeration()) {
            request.setStatus(RequestStatus.PENDING);
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        return ParticipationRequestDtoMapper.mapToParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId)
            throws IncorrectObjectException, IncorrectFieldException, WrongConditionException {
        userService.checkUserExists(userId);
        checkRequestExists(requestId);
        checkCorrectRequester(userId, requestId);
        checkRequestIsCanceled(requestId);

        ParticipationRequest request = requestRepository.getById(requestId);
        request.setStatus(RequestStatus.CANCELED);

        return ParticipationRequestDtoMapper.mapToParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByRequesterId(Long userId) throws IncorrectObjectException {
        userService.checkUserExists(userId);

        return ParticipationRequestDtoMapper.mapToParticipationRequestDto(
                requestRepository.findAllByRequesterId(userId)
        );
    }

    @Override
    public ParticipationRequestDto confirmRequestByInitiator(Long initiatorId, Long eventId, Long reqId)
            throws IncorrectObjectException, IncorrectFieldException, WrongConditionException {
        userService.checkUserExists(initiatorId);
        eventService.checkEventExists(eventId);
        checkRequestExists(reqId);
        eventService.checkUserInitiatorOfEvent(eventId, initiatorId);
        checkCorrectEventRequest(eventId, reqId);
        eventService.checkNullLimitOrNotPreModeration(eventId);
        eventService.checkEventLimit(eventId);
        checkRequestAlreadyConfirmed(reqId);

        ParticipationRequest request = requestRepository.getById(reqId);
        request.setStatus(RequestStatus.CONFIRMED);
        Event event = eventRepository.getById(eventId);
        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        eventRepository.save(event);

        cancelOtherRequests(eventId);

        return ParticipationRequestDtoMapper.mapToParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto rejectRequestByInitiator(Long initiatorId, Long eventId, Long reqId)
            throws IncorrectObjectException, IncorrectFieldException, WrongConditionException {
        userService.checkUserExists(initiatorId);
        eventService.checkEventExists(eventId);
        checkRequestExists(reqId);
        eventService.checkUserInitiatorOfEvent(eventId, initiatorId);
        checkCorrectEventRequest(eventId, reqId);
        checkRequestPending(reqId);

        ParticipationRequest request = requestRepository.getById(reqId);
        request.setStatus(RequestStatus.REJECTED);

        return ParticipationRequestDtoMapper.mapToParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByInitiator(Long initiatorId, Long eventId)
            throws IncorrectObjectException, IncorrectFieldException {
        userService.checkUserExists(initiatorId);
        eventService.checkEventExists(eventId);
        eventService.checkUserInitiatorOfEvent(eventId, initiatorId);

        return ParticipationRequestDtoMapper.mapToParticipationRequestDto(
                requestRepository.findAllByInitiator(initiatorId, eventId)
        );
    }

    private void checkRequestPending(Long reqId) throws WrongConditionException {
        ParticipationRequest request = requestRepository.getById(reqId);
        if (!request.getStatus().equals(RequestStatus.PENDING)) {
            throw new WrongConditionException("Only request in status PENDING can be rejected");
        }
    }

    private void checkRequestAlreadyConfirmed(Long reqId) throws WrongConditionException {
        ParticipationRequest request = requestRepository.getById(reqId);
        if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
            throw new WrongConditionException("Request in already in status CONFIRMED");
        }
    }

    private void checkCorrectEventRequest(Long eventId, Long reqId) throws IncorrectFieldException {
        ParticipationRequest request = requestRepository.getById(reqId);
        if (!Objects.equals(request.getEvent().getId(), eventId)) {
            throw new IncorrectFieldException("Request id = " + reqId + " is not made to event id = " + eventId);
        }
    }

    private void cancelOtherRequests(Long eventId) {
        Event event = eventRepository.getById(eventId);
        int spareParticipantSlots = event.getParticipantLimit() - event.getConfirmedRequests();
        if (spareParticipantSlots == 0) {
            List<ParticipationRequest> notConfirmedRequests = requestRepository
                    .findAllNotConfirmedRequestsByEventId(eventId);
            for (ParticipationRequest request : notConfirmedRequests) {
                request.setStatus(RequestStatus.REJECTED);
                requestRepository.save(request);
            }
        }
    }

    private void checkRequestAlreadyExist(Long userId, Long eventId) {
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new IllegalArgumentException("User id = " + userId + " is the requester of event id = " + eventId);
        }
    }

    private void checkCorrectRequester(Long userId, Long requestId) throws IncorrectFieldException {
        ParticipationRequest request = requestRepository.getById(requestId);
        if (!Objects.equals(request.getRequester().getId(), userId)) {
            throw new IncorrectFieldException("User id = " + userId + " is not the requester of request id = "
                    + requestId);
        }
    }

    private void checkRequestIsCanceled(Long requestId) throws WrongConditionException {
        ParticipationRequest request = requestRepository.getById(requestId);
        if (request.getStatus().equals(RequestStatus.CANCELED)) {
            throw new WrongConditionException("Request in already in status CANCELED");
        }
    }

    private void checkRequestExists(Long requestId) throws IncorrectObjectException {
        if (!requestRepository.findAll().isEmpty()) {
            List<Long> ids = requestRepository.findAll()
                    .stream()
                    .map(ParticipationRequest::getId)
                    .collect(Collectors.toList());
            if (!ids.contains(requestId)) {
                throw new IncorrectObjectException("There is no request with id = " + requestId);
            }
        } else {
            throw new IncorrectObjectException("There is no request with id = " + requestId);
        }
    }
}
