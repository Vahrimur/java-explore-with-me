package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.CompilationService;
import ru.practicum.exception.IncorrectObjectException;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "compilations")
public class NonAuthCompilationController {
    private final CompilationService compilationService;

    //Получение подборок событий
    @GetMapping
    public List<CompilationDto> findCompilations(@RequestParam(required = false) Boolean pinned,
                                                 @RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /compilations?pinned={}&from={}&size={}", pinned, from, size);
        return compilationService.getCompilations(pinned, from, size);
    }

    //Получение подборки событий по его id
    @GetMapping("/{compId}")
    public CompilationDto findCompilationById(@PathVariable Long compId) throws IncorrectObjectException {
        log.info("GET /compilations/{}", compId);
        return compilationService.getCompilationById(compId);
    }
}
