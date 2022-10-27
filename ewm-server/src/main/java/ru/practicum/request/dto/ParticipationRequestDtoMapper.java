package ru.practicum.request.dto;

import ru.practicum.request.model.ParticipationRequest;

import java.util.ArrayList;
import java.util.List;

public class ParticipationRequestDtoMapper {
    public static ParticipationRequestDto mapToParticipationRequestDto(ParticipationRequest request) {
        return new ParticipationRequestDto(request.getId(),
                request.getRequester().getId(),
                request.getEvent().getId(),
                request.getCreated(),
                request.getStatus().toString());
    }

    public static List<ParticipationRequestDto> mapToParticipationRequestDto(Iterable<ParticipationRequest> requests) {
        List<ParticipationRequestDto> participationRequestDtos = new ArrayList<>();
        for (ParticipationRequest request : requests) {
            participationRequestDtos.add(mapToParticipationRequestDto(request));
        }
        return participationRequestDtos;
    }
}
