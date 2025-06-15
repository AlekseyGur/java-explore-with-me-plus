package ru.practicum.main.category.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.CategoryNewDto;
import ru.practicum.main.category.dto.CategoryUpdateDto;
import ru.practicum.main.category.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@AllArgsConstructor
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/{id}")
    public CategoryDto getCategory(@PathVariable @Positive Long id) {
        return categoryService.get(id);
    }

    @GetMapping
    public List<CategoryDto> getAllCategories(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        return categoryService.findAll(from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid CategoryNewDto dto) {
        return categoryService.create(dto);
    }

    @PatchMapping("/{id}")
    public CategoryDto updateCategory(@PathVariable @Positive Long id, @RequestBody @Valid CategoryUpdateDto dto) {
        return categoryService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable @Positive Long id) {
        categoryService.delete(id);
    }
}
