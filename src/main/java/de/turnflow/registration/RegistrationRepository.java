package de.turnflow.registration;

import de.turnflow.registration.entity.Registration;
import de.turnflow.registration.entity.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

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
}