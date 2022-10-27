package ru.practicum.category.dto;

import ru.practicum.category.model.Category;

public class NewCategoryDtoMapper {
    public static Category mapToCategoryEntity(NewCategoryDto newCategoryDto) {
        return new Category(null, newCategoryDto.getName());
    }
}
