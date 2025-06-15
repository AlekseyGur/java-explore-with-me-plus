package ru.practicum.main.category.mapper;

import java.util.List;

import lombok.experimental.UtilityClass;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.CategoryNewDto;
import ru.practicum.main.category.dto.CategoryUpdateDto;

@UtilityClass
public class CategoryMapper {
    public static CategoryDto toDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        return categoryDto;
    }

    public static Category fromDto(CategoryDto categoryDto) {
        Category category = new Category();
        category.setId(categoryDto.getId());
        category.setName(categoryDto.getName());
        return category;
    }

    public static Category fromDto(CategoryNewDto categoryDto) {
        Category category = new Category();
        category.setName(categoryDto.getName());
        return category;
    }

    public static Category fromDto(CategoryUpdateDto categoryDto) {
        Category category = new Category();
        category.setId(categoryDto.getId());
        category.setName(categoryDto.getName());
        return category;
    }

    public static List<Category> fromDto(List<CategoryDto> categorysDto) {
        return categorysDto.stream().map(CategoryMapper::fromDto).toList();
    }

    public static List<CategoryDto> toDto(List<Category> categorys) {
        return categorys.stream().map(CategoryMapper::toDto).toList();
    }
}