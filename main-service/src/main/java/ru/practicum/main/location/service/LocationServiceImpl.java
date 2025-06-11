package ru.practicum.main.location.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.main.location.dto.LocationDto;
import ru.practicum.main.location.dto.LocationNewDto;
import ru.practicum.main.location.dto.LocationUpdateDto;
import ru.practicum.main.location.model.Location;
import ru.practicum.main.location.mapper.LocationMapper;
import ru.practicum.main.location.repository.LocationRepository;
import ru.practicum.main.system.exception.NotFoundException;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;

    @Override
    public List<LocationDto> findAll(Integer from, Integer size) {
        Pageable page = PageRequest.of(from, size);
        return locationRepository.findAll(page).stream()
                .map(LocationMapper::toDto)
                .toList();
    }

    @Override
    public LocationDto findById(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория с таким id не найдена"));
        return LocationMapper.toDto(location);
    }

    @Override
    @Transactional
    public LocationDto create(LocationNewDto dto) {
        Location location = LocationMapper.fromDto(dto);
        return LocationMapper.toDto(locationRepository.save(location));
    }

    @Override
    @Transactional
    public LocationDto update(Long id, LocationUpdateDto dto) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория с таким id не найдена"));

        location.setId(id);
        location.setLat(dto.getLat());
        location.setLon(dto.getLon());

        return LocationMapper.toDto(locationRepository.save(location));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new NotFoundException("Категория с таким id не найдена");
        }
        locationRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return locationRepository.existsById(id);
    }
}
