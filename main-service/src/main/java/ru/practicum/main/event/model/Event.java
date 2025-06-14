package ru.practicum.main.event.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "annotation", columnDefinition = "TEXT")
    private String annotation;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @Column(name = "location_id")
    private Long locationId;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "initiator_id")
    private Long initiatorId;

    @Column(name = "paid")
    private Boolean paid;

    @Column(name = "participant_limit")
    private Long participantLimit;

    @Column(name = "confirmed_requests")
    private Long confirmedRequests;

    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @Column(name = "state")
    private String state;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "views")
    private Long views;
}