package ru.practicum.main.event.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.boot.autoconfigure.security.SecurityProperties;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    String title;

    @Column(name = "annotation")
    String annotation;

    @Column(name = "description")
    String description;

    @Column(name = "event_date")
    LocalDateTime eventDate;

    @Column(name = "location_id")
    Long locationId;

    @Column(name = "category_id")
    Long categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", insertable = false, updatable = false)
    private SecurityProperties.User initiator;

    @Column(name = "paid")
    Boolean paid;

    @Column(name = "participant_limit")
    Long participantLimit;

    @Column(name = "request_moderation")
    Boolean requestModeration;

    @Column(name = "state")
    String state;

    @Column(name = "created_on")
    LocalDateTime createdOn;

    @Column(name = "published_on")
    LocalDateTime publishedOn;

    @Column(name = "views")
    Long views;
}