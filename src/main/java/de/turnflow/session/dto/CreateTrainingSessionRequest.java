package de.turnflow.session.dto;

import de.turnflow.common.validation.ValidDateRange;
import de.turnflow.session.entity.TrainingSessionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@ValidDateRange(from = "startTime", to = "endTime")
public class CreateTrainingSessionRequest {

    @NotNull
    private Long trainingGroupId;

    @NotBlank
    private String title;

    private String description;

    private String location;

    @NotNull
    private OffsetDateTime startTime;

    @NotNull
    private OffsetDateTime endTime;

    private OffsetDateTime registrationDeadline;

    private Integer maxParticipants;

    private boolean waitlistEnabled = true;

    private TrainingSessionStatus status = TrainingSessionStatus.OPEN;
}