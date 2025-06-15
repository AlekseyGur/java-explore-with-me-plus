package ru.practicum.main.category.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.CategoryNewDto;
import ru.practicum.main.category.dto.CategoryUpdateDto;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.mapper.CategoryMapper;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.event.service.EventServiceHelper;
import ru.practicum.main.system.exception.BadConditionsException;
import ru.practicum.main.system.exception.DuplicatedDataException;
import ru.practicum.main.system.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventServiceHelper eventServiceHelper;

    @Override
    public List<CategoryDto> findAll(Integer from, Integer size) {
        Pageable page = PageRequest.of(from, size);
        return categoryRepository.findAll(page).stream()
                .map(CategoryMapper::toDto)
                .toList();
    }

    @Override
    public CategoryDto get(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория с таким id не найдена"));
        return CategoryMapper.toDto(category);
    }

    @Override
    public List<CategoryDto> get(List<Long> ids) {
        return CategoryMapper.toDto(categoryRepository.getByIdIn(ids));
    }

    @Override
    @Transactional
    public CategoryDto create(CategoryNewDto dto) {
        Category category = CategoryMapper.fromDto(dto);

        checkExistsByNameThrowError(dto.getName());

        return CategoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryDto update(Long id, CategoryUpdateDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория с таким id не найдена"));

        Optional<Category> categorySameName = categoryRepository.findByIdAndName(id, dto.getName());

        if (categorySameName.isPresent()) {
            return CategoryMapper.toDto(categorySameName.get());
        }

        checkExistsByNameThrowError(dto.getName());

        category.setName(dto.getName());
        return CategoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("Категория с таким id не найдена");
        }
        if (eventServiceHelper.checkEventsExistInCategory(id)) {
            throw new BadConditionsException("Нельзя удалить категорию с событиями");
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return categoryRepository.existsById(id);
    }

    private void checkExistsByNameThrowError(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new DuplicatedDataException("Категория с таким именем уже существует");
        }
    }
}
