package ru.practicum.request.service;

import ru.practicum.exception.IncorrectFieldException;
import ru.practicum.exception.IncorrectObjectException;
import ru.practicum.exception.WrongConditionException;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto createRequest(Long userId, Long eventId)
            throws IncorrectObjectException, IncorrectFieldException, WrongConditionException;

    ParticipationRequestDto cancelRequest(Long userId, Long requestId)
            throws IncorrectObjectException, IncorrectFieldException, WrongConditionException;

    List<ParticipationRequestDto> getRequestsByRequesterId(Long userId) throws IncorrectObjectException;

    ParticipationRequestDto confirmRequestByInitiator(Long initiatorId, Long eventId, Long reqId)
            throws IncorrectObjectException, IncorrectFieldException, WrongConditionException;

    ParticipationRequestDto rejectRequestByInitiator(Long userId, Long eventId, Long reqId)
            throws IncorrectObjectException, IncorrectFieldException, WrongConditionException;

    List<ParticipationRequestDto> getRequestsByInitiator(Long userId, Long eventId)
            throws IncorrectObjectException, IncorrectFieldException;
}
