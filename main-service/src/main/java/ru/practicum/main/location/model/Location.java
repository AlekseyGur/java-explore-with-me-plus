package ru.practicum.main.location.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "lat", nullable = false, precision = 10, scale = 8)
    private Double lat;

    @Column(name = "lon", nullable = false, precision = 11, scale = 8)
    private Double lon;
}
