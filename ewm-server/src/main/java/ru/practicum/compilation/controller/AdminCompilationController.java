package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.service.CompilationService;
import ru.practicum.exception.IncorrectFieldException;
import ru.practicum.exception.IncorrectObjectException;
import ru.practicum.exception.WrongConditionException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "admin/compilations")
public class AdminCompilationController {
    private final CompilationService compilationService;

    //Добавление новой подборки
    @PostMapping
    public CompilationDto addCompilation(@RequestBody NewCompilationDto newCompilationDto)
            throws IncorrectObjectException, IncorrectFieldException {
        log.info("POST /admin/compilations {}", newCompilationDto);
        return compilationService.createCompilation(newCompilationDto);
    }

    //Удаление подборки
    @DeleteMapping("/{compId}")
    public void deleteCompilation(@PathVariable Long compId) throws IncorrectObjectException {
        log.info("DELETE /admin/compilations/{}", compId);
        compilationService.deleteCompilation(compId);
    }

    //Добавление события в подборку
    @PatchMapping("/{compId}/events/{eventId}")
    public void addEventToCompilation(@PathVariable Long compId,
                                      @PathVariable Long eventId) throws IncorrectObjectException {
        log.info("PATCH /admin/compilations/{}/events/{}", compId, eventId);
        compilationService.addEventToCompilation(compId, eventId);
    }

    //Удаление события из подборки
    @DeleteMapping("/{compId}/events/{eventId}")
    public void deleteEventFromCompilation(@PathVariable Long compId,
                                           @PathVariable Long eventId) throws IncorrectObjectException {
        log.info("DELETE /admin/compilations/{}/events/{}", compId, eventId);
        compilationService.deleteEventFromCompilation(compId, eventId);
    }

    //Закрепление подборки на главной странице
    @PatchMapping("/{compId}/pin")
    public void pinCompilation(@PathVariable Long compId) throws WrongConditionException, IncorrectObjectException {
        log.info("PATCH /admin/compilations/{}/pin", compId);
        compilationService.pinCompilation(compId);
    }

    //Открепление подборки на главной странице
    @DeleteMapping("/{compId}/pin")
    public void unpinCompilation(@PathVariable Long compId) throws WrongConditionException, IncorrectObjectException {
        log.info("DELETE /admin/compilations/{}/pin", compId);
        compilationService.unpinCompilation(compId);
    }
}
