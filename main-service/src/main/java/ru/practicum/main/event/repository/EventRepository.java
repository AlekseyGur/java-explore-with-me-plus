package ru.practicum.main.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import ru.practicum.main.event.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    List<Event> findByIdIn(List<Long> userIds);

    List<Event> findByIdInAndState(List<Long> userIds, String state);

    Optional<Event> findByIdAndState(Long id, String state);

    Page<Event> findAllByInitiatorId(Long userId, Pageable page);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    Page<Event> findAll(Specification<Event> spec, Pageable pageable);

    boolean existsByIdAndInitiatorId(Long eventId, Long userId);

    boolean existsByCategoryId(Long categoryId);

    boolean existsById(@NonNull Long eventId);

    @Modifying
    @Query("UPDATE Event e SET e.views = :views WHERE e.id = :eventId")
    void setViews(@Param("eventId") Long eventId, @Param("views") Long views);

    @Modifying
    @Query("UPDATE Event e SET e.confirmedRequests = :count WHERE e.id = :eventId")
    void setContirmedRequestsCount(
            @Param("eventId") Long eventId,
            @Param("count") Long count);
}
