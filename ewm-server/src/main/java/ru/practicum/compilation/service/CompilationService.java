package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.exception.IncorrectFieldException;
import ru.practicum.exception.IncorrectObjectException;
import ru.practicum.exception.WrongConditionException;

import java.util.List;

public interface CompilationService {
    CompilationDto createCompilation(NewCompilationDto newCompilationDto)
            throws IncorrectObjectException, IncorrectFieldException;

    void deleteCompilation(Long compId) throws IncorrectObjectException;

    void checkCompilationExists(Long compId) throws IncorrectObjectException;

    void addEventToCompilation(Long compId, Long eventId) throws IncorrectObjectException;

    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Long compId) throws IncorrectObjectException;

    void deleteEventFromCompilation(Long compId, Long eventId) throws IncorrectObjectException;

    void pinCompilation(Long compId) throws IncorrectObjectException, WrongConditionException;

    void unpinCompilation(Long compId) throws IncorrectObjectException, WrongConditionException;
}
