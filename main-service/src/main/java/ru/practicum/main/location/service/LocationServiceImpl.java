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
    public LocationDto getByLonAndLat(Float lon, Float lat) {
        return locationRepository.findByLonAndLat(lon, lat)
                .map(LocationMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Место с таким lon-lat не найдено"));
    }

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
                .orElseThrow(() -> new NotFoundException("Место с таким id не найдено"));
        return LocationMapper.toDto(location);
    }

    @Override
    public List<LocationDto> get(List<Long> ids) {
        return locationRepository.getByIdIn(ids).stream().map(LocationMapper::toDto).toList();
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
                .orElseThrow(() -> new NotFoundException("Место с таким id не найдено"));

        location.setId(id);
        location.setLat(dto.getLat());
        location.setLon(dto.getLon());

        return LocationMapper.toDto(locationRepository.save(location));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new NotFoundException("Место с таким id не найдено");
        }
        locationRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return locationRepository.existsById(id);
    }

    @Override
    public boolean existsByLonAndLat(Float lon, Float lat) {
        return locationRepository.existsByLonAndLat(lon, lat);
    }
}
