package ru.practicum.main.category.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.category.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> getByIdIn(List<Long> ids);

    Optional<Category> findByIdAndName(Long ids, String name);

    boolean existsByName(String name);
}
