package ru.practicum.main.compilations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.compilations.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
}
