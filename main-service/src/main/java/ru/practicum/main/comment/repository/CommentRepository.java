package ru.practicum.main.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.practicum.main.comment.model.Comment;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

        List<Comment> getAllByEventId(Long eventId);

        List<Comment> getAllByUserId(Long userId);

        @Query("SELECT c FROM Comment c " +
                        "WHERE (:rangeStart IS NULL OR c.createdOn >= :rangeStart) " +
                        "AND (:rangeEnd IS NULL OR c.createdOn <= :rangeEnd)")
        List<Comment> findAllByCreatedOnBetween(
                        LocalDateTime rangeStart,
                        LocalDateTime rangeEnd,
                        Pageable pageable);

        @Modifying
        @Query("UPDATE Comment c SET c.text = :text WHERE c.id = :id")
        void updateTextById(@Param("id") Long id, @Param("text") String text);

}
