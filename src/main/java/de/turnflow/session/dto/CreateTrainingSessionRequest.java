package de.turnflow.session.dto;

import de.turnflow.common.validation.ValidDateRange;

import de.turnflow.common.validation.ValidRegistrationDeadline;
import de.turnflow.session.entity.TrainingSessionStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@ValidDateRange(from = "startTime", to = "endTime")
@ValidRegistrationDeadline
public class CreateTrainingSessionRequest {

    @NotNull
    private Long trainingGroupId;

    @NotBlank
    @Size(max = 150)
    private String title;

    @Size(max = 2000)
    private String description;

    @Size(max = 200)
    private String location;

    @NotNull
    private OffsetDateTime startTime;

    @NotNull
    private OffsetDateTime endTime;

    private OffsetDateTime registrationDeadline;

    @Min(1)
    private Integer maxParticipants;

    private boolean waitlistEnabled = true;

    private TrainingSessionStatus status = TrainingSessionStatus.OPEN;
}