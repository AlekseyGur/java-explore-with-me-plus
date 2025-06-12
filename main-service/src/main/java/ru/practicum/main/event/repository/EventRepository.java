package ru.practicum.main.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.practicum.main.event.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByIdInAndState(List<Long> userIds, String state);

    Optional<Event> findByIdAndState(Long id, String state);

    Page<Event> findAllByInitiatorId(Long userId, Pageable page);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    Page<Event> findAll(Specification<Event> spec, Pageable pageable);

    boolean existsByIdAndInitiatorId(Long eventId, Long userId);

    @Modifying
    @Query("UPDATE Event e SET e.views = e.views + 1 WHERE e.id = :eventId")
    void increaseViews(@Param("eventId") Long eventId);
}
