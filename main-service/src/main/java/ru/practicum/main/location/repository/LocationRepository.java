package ru.practicum.main.location.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.practicum.main.location.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByLonAndLat(Float lon, Float lat);

    List<Location> getByIdIn(List<Long> ids);

    boolean existsByLonAndLat(Float lon, Float lat);
}
