package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.exception.IncorrectFieldException;
import ru.practicum.exception.IncorrectObjectException;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryDto newCategoryDto) throws IncorrectFieldException;

    CategoryDto updateCategory(CategoryDto categoryDto) throws IncorrectObjectException, IncorrectFieldException;

    void deleteCategoryById(Long categoryId) throws IncorrectObjectException;

    void checkCategoryExist(Long categoryId) throws IncorrectObjectException;

    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Long catId) throws IncorrectObjectException;
}
