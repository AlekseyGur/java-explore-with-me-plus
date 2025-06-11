package ru.practicum.main.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.location.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
