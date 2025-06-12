package ru.practicum.main.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.practicum.main.request.model.ParticipationRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findAllByRequesterId(Long requesterId);

    List<ParticipationRequest> findAllByEventId(Long eventId);

    Optional<ParticipationRequest> findByEventIdAndIdIn(Long eventId, List<Long> requestsId);

    Optional<ParticipationRequest> findByEventIdAndRequesterId(Long eventId, Long requestsId);

    @Query("SELECT r.eventId, COUNT(r.id) " +
            "FROM Request r " +
            "WHERE r.eventId IN :eventIds " +
            "AND r.status = :status " +
            "GROUP BY r.eventId")
    Map<Long, Long> getCountByEventIdInAndStatus(
            @Param("eventIds") List<Long> eventIds,
            @Param("status") String status);
}