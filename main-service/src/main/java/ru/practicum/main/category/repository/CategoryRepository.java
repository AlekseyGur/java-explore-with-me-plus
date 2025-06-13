package ru.practicum.main.category.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.category.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> getByIdIn(List<Long> Ids);

    boolean existsByName(String name);
}
