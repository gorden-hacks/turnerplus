package de.turnflow.session.dto;

import de.turnflow.registration.entity.RegistrationStatus;
import de.turnflow.session.entity.TrainingSessionActionDisabledReason;
import de.turnflow.session.entity.TrainingSessionStatus;
import lombok.*;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyTrainingSessionDto {

    private Long id;

    private Long trainingGroupId;
    private String trainingGroupName;

    private String title;
    private String location;

    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private OffsetDateTime registrationDeadline;

    private Integer maxParticipants;
    private long registeredCount;
    private long waitlistCount;

    private TrainingSessionStatus status;
    private RegistrationStatus myRegistrationStatus;

    private boolean canRegister;
    private boolean canUnregister;

    private TrainingSessionActionDisabledReason registerDisabledReason;
    private TrainingSessionActionDisabledReason unregisterDisabledReason;
}