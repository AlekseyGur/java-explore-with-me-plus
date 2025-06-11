package ru.practicum.main.event.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import ru.practicum.main.event.model.Event;

class EventSpecs {
    public Specification<Event> hasText(String text) {
        return (root, query, cb) -> cb.like(root.get("name"), "%" + text + "%");
    }

    public Specification<Event> hasCategories(List<Long> categories) {
        return (root, query, cb) -> root.get("category").in(categories);
    }

    public Specification<Event> hasPaid(Boolean paid) {
        return (root, query, cb) -> cb.equal(root.get("paid"), paid);
    }

    public Specification<Event> hasRangeStart(LocalDateTime start) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("eventDate"),
                start);
    }

    public Specification<Event> hasRangeEnd(LocalDateTime end) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("eventDate"), end);
    }

    public Specification<Event> hasAvailable(Boolean available) {
        return (root, query, cb) -> cb.equal(root.get("available"), available);
    }
}