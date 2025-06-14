package ru.practicum.main.category.service;

import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.CategoryNewDto;
import ru.practicum.main.category.dto.CategoryUpdateDto;

import java.util.List;

public interface CategoryService {

    CategoryDto get(Long id);

    List<CategoryDto> get(List<Long> ids);

    List<CategoryDto> findAll(Integer from, Integer size);

    CategoryDto create(CategoryNewDto dto);

    CategoryDto update(Long id, CategoryUpdateDto dto);

    void delete(Long id);

    boolean existsById(Long id);
}
