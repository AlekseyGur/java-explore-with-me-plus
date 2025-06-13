package ru.practicum.main.views.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.practicum.main.views.model.View;

public interface ViewRepository extends JpaRepository<View, Long> {
    boolean existsByIpAndEventId(String ip, Long eventId);

    Long countByEventId(Long eventId);
}
