package de.turnflow.session;

import de.turnflow.session.entity.TrainingSession;
import de.turnflow.session.entity.TrainingSessionStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {

    @Query("""
            select s
            from TrainingSession s
            join fetch s.trainingGroup g
            where (:groupId is null or g.id = :groupId)
              and (:status is null or s.status = :status)
              and (:from is null or s.endTime >= :from)
              and (:to is null or s.startTime <= :to)
            order by s.startTime asc
            """)
    List<TrainingSession> findFiltered(
            @Param("groupId") Long groupId,
            @Param("status") TrainingSessionStatus status,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to
    );

    @Query("""
            select s
            from TrainingSession s
            join fetch s.trainingGroup g
            where s.startTime >= :from
              and s.startTime < :to
              and (:groupId is null or g.id = :groupId)
            order by s.startTime asc
            """)
    List<TrainingSession> findCalendarSessions(
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to,
            @Param("groupId") Long groupId
    );
}