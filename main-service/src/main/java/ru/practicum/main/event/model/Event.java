package ru.practicum.main.event.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @jakarta.persistence.Column(name = "id")
    Long id;

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

    @Column(name = "initiator_id")
    Long initiatorId;

    @Column(name = "paid")
    Boolean paid;

    @Column(name = "participant_limit")
    Long participantLimit;

    @Column(name = "request_moderation")
    Boolean requestModeration;

    @Column(name = "state_id")
    Integer stateId;

    @Column(name = "createdOn")
    LocalDateTime createdOn;

    @Column(name = "published_on")
    LocalDateTime publishedOn;

    @Column(name = "views")
    Long views;
}