package ru.practicum.category.dto;

import ru.practicum.category.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryDtoMapper {
    public static CategoryDto mapToCategoryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }

    public static List<CategoryDto> mapToCategoryDto(Iterable<Category> categories) {
        List<CategoryDto> categoryDtos = new ArrayList<>();
        for (Category category : categories) {
            categoryDtos.add(mapToCategoryDto(category));
        }
        return categoryDtos;
    }

    public static Category mapToCategoryEntity(CategoryDto categoryDto) {
        return new Category(
                categoryDto.getId(),
                categoryDto.getName()
        );
    }
}
