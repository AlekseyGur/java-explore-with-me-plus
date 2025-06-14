package ru.practicum.main.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.practicum.main.request.model.ParticipationRequest;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findAllByRequesterId(Long requesterId);

    List<ParticipationRequest> findAllByEventId(Long eventId);

    List<ParticipationRequest> findAllByIdIn(List<Long> ids);

    List<ParticipationRequest> findAllByEventIdAndStatus(Long eventId, String status);

    List<ParticipationRequest> findByEventIdAndIdIn(Long eventId, List<Long> requestsId);

    Optional<ParticipationRequest> findByEventIdAndRequesterId(Long eventId, Long requestsId);

    @Modifying
    @Query("UPDATE ParticipationRequest r SET r.status = :status WHERE r.id IN :ids")
    void setStatusAll(@Param("ids") List<Long> ids, @Param("status") String status);

    @Query("SELECT r.eventId as eventId, COUNT(r.id) as count " +
                    "FROM ParticipationRequest r " +
                    "WHERE r.eventId IN :eventIds " +
                    "AND r.status = :status " +
                    "GROUP BY r.eventId")
    List<Object[]> getCountByEventIdInAndStatus(
                    @Param("eventIds") List<Long> eventIds,
                    @Param("status") String status);
}