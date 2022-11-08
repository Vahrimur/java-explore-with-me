package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationDtoMapper;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.NewCompilationDtoMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.CompilationEvent;
import ru.practicum.compilation.repository.CompilationEventRepository;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.IncorrectFieldException;
import ru.practicum.exception.IncorrectObjectException;
import ru.practicum.exception.WrongConditionException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationEventRepository compilationEventRepository;
    private final EventService eventService;

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto)
            throws IncorrectObjectException, IncorrectFieldException {
        checkNewCompilationTitle(newCompilationDto);

        for (Long eventId : newCompilationDto.getEvents()) {
            eventService.checkEventExists(eventId);
        }

        Compilation compilation = compilationRepository.save(
                NewCompilationDtoMapper.mapToCompilationEntity(newCompilationDto)
        );

        Long compilationId = compilation.getId();
        for (Long eventId : newCompilationDto.getEvents()) {
            CompilationEvent compilationEvent = new CompilationEvent(null, compilationId, eventId);
            compilationEventRepository.save(compilationEvent);
        }

        List<EventShortDto> eventsShortDtos = eventService.getEventsByCompilationId(compilationId);

        return CompilationDtoMapper.mapToCompilationDto(compilation, eventsShortDtos);
    }

    @Override
    public void deleteCompilationById(Long compId) throws IncorrectObjectException {
        checkCompilationExists(compId);

        compilationRepository.deleteById(compId);
    }

    @Override
    public void addEventToCompilation(Long compId, Long eventId) throws IncorrectObjectException {
        checkCompilationExists(compId);
        eventService.checkEventExists(eventId);

        compilationEventRepository.save(new CompilationEvent(null, compId, eventId));
    }

    @Override
    public void deleteEventFromCompilation(Long compId, Long eventId) throws IncorrectObjectException {
        checkCompilationExists(compId);
        eventService.checkEventExists(eventId);
        checkEventIsInCompilation(compId, eventId);

        compilationEventRepository.deleteByCompilationIdAndEventId(compId, eventId);
    }

    @Override
    public void pinCompilationById(Long compId) throws IncorrectObjectException, WrongConditionException {
        checkCompilationExists(compId);
        checkCompilationPinned(compId);

        Compilation compilation = compilationRepository.getById(compId);
        compilation.setPinned(true);
        compilationRepository.save(compilation);
    }

    @Override
    public void unpinCompilationById(Long compId) throws IncorrectObjectException, WrongConditionException {
        checkCompilationExists(compId);
        checkCompilationNotPinned(compId);

        Compilation compilation = compilationRepository.getById(compId);
        compilation.setPinned(false);
        compilationRepository.save(compilation);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        return compilationRepository.findAllByParams(pinned, from, size)
                .stream()
                .map(c -> CompilationDtoMapper.mapToCompilationDto(c, eventService.getEventsByCompilationId(c.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) throws IncorrectObjectException {
        checkCompilationExists(compId);

        return CompilationDtoMapper.mapToCompilationDto(
                compilationRepository.getById(compId), eventService.getEventsByCompilationId(compId)
        );
    }

    private void checkEventIsInCompilation(Long compId, Long eventId) {
        if (!compilationEventRepository.existsByCompilationIdAndEventId(compId, eventId)) {
            throw new IllegalArgumentException("Event id = " + eventId + " is not in compilation id = " + compId);
        }
    }

    private void checkNewCompilationTitle(NewCompilationDto newCompilationDto) throws IncorrectFieldException {
        if (newCompilationDto.getTitle() == null || newCompilationDto.getTitle().isBlank()) {
            throw new IncorrectFieldException("Title field cannot be blank");
        }
    }

    @Override
    public void checkCompilationExists(Long compId) throws IncorrectObjectException {
        if (!compilationRepository.findAll().isEmpty()) {
            List<Long> ids = compilationRepository.findAll()
                    .stream()
                    .map(Compilation::getId)
                    .collect(Collectors.toList());
            if (!ids.contains(compId)) {
                throw new IncorrectObjectException("There is no compilation with id = " + compId);
            }
        } else {
            throw new IncorrectObjectException("There is no compilation with id = " + compId);
        }
    }

    private void checkCompilationPinned(Long compId) throws WrongConditionException {
        if (compilationRepository.getById(compId).getPinned()) {
            throw new WrongConditionException("Compilation id = " + compId + " is already pinned.");
        }
    }

    private void checkCompilationNotPinned(Long compId) throws WrongConditionException {
        if (!compilationRepository.getById(compId).getPinned()) {
            throw new WrongConditionException("Compilation id = " + compId + " is not pinned.");
        }
    }
}
