package ru.practicum.main.category.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.CategoryNewDto;
import ru.practicum.main.category.dto.CategoryUpdateDto;
import ru.practicum.main.category.service.CategoryService;

@RestController
@AllArgsConstructor
@RequestMapping("/admin/categories")
@Validated
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@RequestBody @Valid CategoryNewDto newCategoryDto) {
        return categoryService.create(newCategoryDto);
    }

    @PatchMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto update(@PathVariable @Positive Long categoryId,
                    @RequestBody @Valid CategoryUpdateDto categoryDto) {
        return categoryService.update(categoryId, categoryDto);
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long categoryId) {
        categoryService.delete(categoryId);
    }
}
