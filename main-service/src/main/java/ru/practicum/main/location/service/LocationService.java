package ru.practicum.main.location.service;

import ru.practicum.main.location.dto.LocationDto;
import ru.practicum.main.location.dto.LocationNewDto;
import ru.practicum.main.location.dto.LocationUpdateDto;

import java.util.List;

public interface LocationService {
    LocationDto getByLonAndLat(Float lon, Float lat);

    LocationDto findById(Long id);

    List<LocationDto> findAll(Integer from, Integer size);

    LocationDto create(LocationNewDto dto);

    LocationDto update(Long id, LocationUpdateDto dto);

    void delete(Long id);

    boolean existsById(Long id);

    boolean existsByLonAndLat(Float lon, Float lat);

    List<LocationDto> get(List<Long> ids);
}
