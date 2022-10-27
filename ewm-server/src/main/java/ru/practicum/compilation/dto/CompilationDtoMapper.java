package ru.practicum.compilation.dto;

import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dto.EventShortDto;

import java.util.List;

public class CompilationDtoMapper {

    public static CompilationDto mapToCompilationDto(Compilation compilation, List<EventShortDto> compilationEvents) {
        return new CompilationDto(
                compilation.getId(),
                compilation.getTitle(),
                compilation.getPinned(),
                compilationEvents
        );
    }
}
