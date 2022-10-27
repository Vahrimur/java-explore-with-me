package ru.practicum.category.controller;

import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;
import ru.practicum.exception.IncorrectObjectException;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "categories")
public class NonAuthCategoryController {
    private final CategoryService categoryService;

    //Получение категорий
    @GetMapping
    public List<CategoryDto> findCategories(@RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /categories");
        return categoryService.getCategories(from, size);
    }

    //Получение информации о категории по ее идентификатору
    @GetMapping(value = "/{catId}")
    public CategoryDto findCategory(@PathVariable @NotNull Long catId) throws IncorrectObjectException {
        log.info("GET /categories/" + catId);
        return categoryService.getCategoryById(catId);
    }
}
