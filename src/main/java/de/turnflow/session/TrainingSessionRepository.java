package de.turnflow.session;

import de.turnflow.registration.entity.RegistrationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import de.turnflow.session.entity.TrainingSession;
import de.turnflow.session.entity.TrainingSessionStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long>,
        JpaSpecificationExecutor<TrainingSession> {

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

    @Query(
            value = """
                select
                    s.id as id,
                    g.id as trainingGroupId,
                    g.name as trainingGroupName,
                    s.title as title,
                    s.description as description,
                    s.location as location,
                    s.startTime as startTime,
                    s.endTime as endTime,
                    s.registrationDeadline as registrationDeadline,
                    s.maxParticipants as maxParticipants,
                    s.waitlistEnabled as waitlistEnabled,
                    s.status as status,
                    coalesce(sum(case when r.status = :registeredStatus then 1 else 0 end), 0) as registeredCount,
                    coalesce(sum(case when r.status = :waitlistStatus then 1 else 0 end), 0) as waitlistCount
                from TrainingSession s
                join s.trainingGroup g
                left join Registration r on r.trainingSession = s
                where (:groupId is null or g.id = :groupId)
                  and (:status is null or s.status = :status)
                  and (:from is null or s.endTime >= :from)
                  and (:to is null or s.startTime <= :to)
                group by s.id, g.id, g.name
                """,
            countQuery = """
                select count(s)
                from TrainingSession s
                join s.trainingGroup g
                where (:groupId is null or g.id = :groupId)
                  and (:status is null or s.status = :status)
                  and (:from is null or s.endTime >= :from)
                  and (:to is null or s.startTime <= :to)
                """
    )
    Page<TrainingSessionWithCountsProjection> findFilteredWithCounts(
            @Param("groupId") Long groupId,
            @Param("status") TrainingSessionStatus status,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to,
            @Param("registeredStatus") RegistrationStatus registeredStatus,
            @Param("waitlistStatus") RegistrationStatus waitlistStatus,
            Pageable pageable
    );

    @Query("""
        select
            s.id as id,
            g.id as trainingGroupId,
            g.name as trainingGroupName,
            s.title as title,
            s.description as description,
            s.location as location,
            s.startTime as startTime,
            s.endTime as endTime,
            s.registrationDeadline as registrationDeadline,
            s.maxParticipants as maxParticipants,
            s.waitlistEnabled as waitlistEnabled,
            s.status as status,
            coalesce(sum(case when r.status = :registeredStatus then 1 else 0 end), 0) as registeredCount,
            coalesce(sum(case when r.status = :waitlistStatus then 1 else 0 end), 0) as waitlistCount
        from TrainingSession s
        join s.trainingGroup g
        left join Registration r on r.trainingSession = s
        where s.startTime >= :from
          and s.startTime < :to
          and (:groupId is null or g.id = :groupId)
        group by s.id, g.id, g.name
        order by s.startTime asc
        """)
    List<TrainingSessionWithCountsProjection> findCalendarSessionsWithCounts(
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to,
            @Param("groupId") Long groupId,
            @Param("registeredStatus") RegistrationStatus registeredStatus,
            @Param("waitlistStatus") RegistrationStatus waitlistStatus
    );

    @Query("""
        select s
        from TrainingSession s
        join fetch s.trainingGroup g
        where exists (
            select 1
            from MemberGroupPermission p
            where p.member.id = :memberId
              and p.trainingGroup.id = g.id
              and p.active = true
              and p.validFrom <= current_date
              and (p.validTo is null or p.validTo >= current_date)
        )
          and (:from is null or s.endTime >= :from)
          and (:to is null or s.startTime <= :to)
        order by s.startTime asc
        """)
    List<TrainingSession> findVisibleForMember(
            Long memberId,
            OffsetDateTime from,
            OffsetDateTime to
    );
}