package ru.practicum.main.views.mapper;

import java.util.List;

import lombok.experimental.UtilityClass;
import ru.practicum.main.location.dto.LocationDto;
import ru.practicum.main.location.dto.LocationNewDto;
import ru.practicum.main.location.dto.LocationUpdateDto;
import ru.practicum.main.location.model.Location;

@UtilityClass
public class LocationMapper {
    public static LocationDto toDto(Location location) {
        LocationDto locationDto = new LocationDto();
        locationDto.setId(location.getId());
        locationDto.setLat(location.getLat());
        locationDto.setLon(location.getLon());
        return locationDto;
    }

    public static Location fromDto(LocationDto locationDto) {
        Location location = new Location();
        location.setId(locationDto.getId());
        location.setLat(locationDto.getLat());
        location.setLon(locationDto.getLon());
        return location;
    }

    public static Location fromDto(LocationNewDto locationDto) {
        Location location = new Location();
        location.setLat(locationDto.getLat());
        location.setLon(locationDto.getLon());
        return location;
    }

    public static Location fromDto(LocationUpdateDto locationDto) {
        Location location = new Location();
        location.setId(locationDto.getId());
        location.setLat(locationDto.getLat());
        location.setLon(locationDto.getLon());
        return location;
    }

    public static LocationNewDto fromUpdateToNew(LocationUpdateDto locationDto) {
        LocationNewDto location = new LocationNewDto();
        location.setLat(locationDto.getLat());
        location.setLon(locationDto.getLon());
        return location;
    }

    public static List<Location> fromDto(List<LocationDto> locationsDto) {
        return locationsDto.stream().map(LocationMapper::fromDto).toList();
    }

    public static List<LocationDto> toDto(List<Location> locations) {
        return locations.stream().map(LocationMapper::toDto).toList();
    }
}