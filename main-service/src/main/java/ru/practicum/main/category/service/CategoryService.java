package ru.practicum.main.category.service;

import ru.practicum.main.category.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto findById(Long id);

    List<CategoryDto> findAll(Integer from, Integer size);

    CategoryDto create(CategoryDto dto);

    CategoryDto update(Long id, CategoryDto dto);

    void delete(Long id);

    boolean existsById(Long id);
}
