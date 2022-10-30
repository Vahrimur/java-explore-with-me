package ru.practicum.category.controller;

import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.service.CategoryService;
import ru.practicum.exception.IncorrectFieldException;
import ru.practicum.exception.IncorrectObjectException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "admin/categories")
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public CategoryDto addCategory(@RequestBody NewCategoryDto newCategoryDto) throws IncorrectFieldException {
        CategoryDto categoryDto = categoryService.createCategory(newCategoryDto);
        log.info("POST /admin/categories {}", categoryDto);
        return categoryDto;
    }

    @PatchMapping
    public CategoryDto updateCategory(@RequestBody CategoryDto categoryDto)
            throws IncorrectObjectException, IncorrectFieldException {
        categoryDto = categoryService.updateCategory(categoryDto);
        log.info("PATCH /admin/categories {}", categoryDto);
        return categoryDto;
    }

    @DeleteMapping(value = "/{catId}")
    public void removeCategoryById(@PathVariable @NotNull Long catId) throws IncorrectObjectException {
        log.info("DELETE /admin/categories/" + catId);
        categoryService.deleteCategoryById(catId);
    }
}
