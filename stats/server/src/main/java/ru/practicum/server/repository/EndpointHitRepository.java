package ru.practicum.server.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.practicum.server.model.EndpointHit;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {
    @Query("SELECT e FROM EndpointHit e WHERE e.timestamp BETWEEN :start AND :end")
    List<EndpointHit> findByTimestampBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT h.app, h.uri, COUNT(DISTINCT h.ip) as hits " +
                    "FROM EndpointHit h " +
                    "WHERE (:uris IS NULL OR h.uri IN (:uris)) " +
                    "AND (h.timestamp BETWEEN :start AND :end) " +
                    "GROUP BY h.app, h.uri " +
                    "ORDER BY hits DESC")
    List<Object[]> findHitsUnique(
                    @Param("start") LocalDateTime start,
                    @Param("end") LocalDateTime end,
                    @Param("uris") List<String> uris);

    @Query("SELECT h.app, h.uri, COUNT(h.ip) AS hits " +
                    "FROM EndpointHit h " +
                    "WHERE (:uris IS NULL OR h.uri IN (:uris)) " +
                    "AND (h.timestamp BETWEEN :start AND :end)" +
                    "GROUP BY h.app, h.uri " +
                    "ORDER BY hits DESC ")
    List<Object[]> findHits(
                    @Param("start") LocalDateTime start,
                    @Param("end") LocalDateTime end,
                    @Param("uris") List<String> uris);
}