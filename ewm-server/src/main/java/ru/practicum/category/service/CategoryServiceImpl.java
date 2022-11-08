package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryDtoMapper;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.dto.NewCategoryDtoMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exception.IncorrectFieldException;
import ru.practicum.exception.IncorrectObjectException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) throws IncorrectFieldException {
        checkNewCategoryData(newCategoryDto);

        Category category = NewCategoryDtoMapper.mapToCategoryEntity(newCategoryDto);
        return CategoryDtoMapper.mapToCategoryDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto)
            throws IncorrectObjectException, IncorrectFieldException {
        checkIdExists(categoryDto.getId());
        checkCategoryExist(categoryDto.getId());

        Category category = CategoryDtoMapper.mapToCategoryEntity(categoryDto);
        return CategoryDtoMapper.mapToCategoryDto(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        checkCorrectParams(from, size);

        List<Category> categories = categoryRepository.findAllByParams(from, size);
        return CategoryDtoMapper.mapToCategoryDto(categories);
    }

    @Override
    public CategoryDto getCategoryById(Long catId) throws IncorrectObjectException {
        checkCategoryExist(catId);

        return CategoryDtoMapper.mapToCategoryDto(categoryRepository.getById(catId));
    }

    @Override
    public void deleteCategoryById(Long catId) throws IncorrectObjectException {
        checkCategoryExist(catId);

        categoryRepository.deleteById(catId);
    }

    private void checkNewCategoryData(NewCategoryDto newCategoryDto) throws IncorrectFieldException {
        if (newCategoryDto.getName() == null || newCategoryDto.getName().isBlank()) {
            throw new IncorrectFieldException("Name field cannot be blank");
        }
    }

    @Override
    public void checkCategoryExist(Long catId) throws IncorrectObjectException {
        if (!categoryRepository.findAll().isEmpty()) {
            List<Long> ids = categoryRepository.findAll()
                    .stream()
                    .map(Category::getId)
                    .collect(Collectors.toList());
            if (!ids.contains(catId)) {
                throw new IncorrectObjectException("There is no category with id = " + catId);
            }
        } else {
            throw new IncorrectObjectException("There is no category with such id = " + catId);
        }
    }

    private void checkIdExists(Long categoryId) throws IncorrectFieldException {
        if (categoryId == null) {
            throw new IncorrectFieldException("Id field cannot be null");
        }
    }

    private void checkCorrectParams(Integer from, Integer size) {
        if (from < 0) {
            throw new IllegalArgumentException("From parameter cannot be less zero");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Size parameter cannot be less or equal zero");
        }
    }
}
