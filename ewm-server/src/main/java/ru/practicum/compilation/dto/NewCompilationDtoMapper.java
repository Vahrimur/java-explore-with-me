package ru.practicum.compilation.dto;

import ru.practicum.compilation.model.Compilation;

public class NewCompilationDtoMapper {

    public static Compilation mapToCompilationEntity(NewCompilationDto newCompilationDto) {
        return new Compilation(
                null,
                newCompilationDto.getTitle(),
                newCompilationDto.getPinned()
        );
    }
}
