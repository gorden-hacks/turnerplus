package de.turnflow.registration;

import de.turnflow.registration.entity.Registration;
import de.turnflow.registration.entity.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    Optional<Registration> findByTrainingSessionIdAndMemberId(
            Long trainingSessionId,
            Long memberId
    );

    long countByTrainingSessionIdAndStatus(
            Long trainingSessionId,
            RegistrationStatus status
    );

    @Query("""
            select
                r.trainingSession.id as trainingSessionId,
                sum(case when r.status = :registeredStatus then 1 else 0 end) as registeredCount,
                sum(case when r.status = :waitlistStatus then 1 else 0 end) as waitlistCount
            from Registration r
            where r.trainingSession.id in :sessionIds
            group by r.trainingSession.id
            """)
    List<RegistrationCountProjection> countByTrainingSessionIds(
            @Param("sessionIds") Collection<Long> sessionIds,
            @Param("registeredStatus") RegistrationStatus registeredStatus,
            @Param("waitlistStatus") RegistrationStatus waitlistStatus
    );

    List<Registration> findByMemberIdAndTrainingSessionIdIn(
            Long memberId,
            Collection<Long> trainingSessionIds
    );
}