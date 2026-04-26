package de.turnflow.session.dto;

import de.turnflow.common.validation.ValidDateRange;
import de.turnflow.common.validation.ValidRegistrationDeadline;
import de.turnflow.session.entity.TrainingSessionStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@ValidDateRange(from = "startTime", to = "endTime")
@ValidRegistrationDeadline
public class UpdateTrainingSessionRequest {

    private Long trainingGroupId;

    @Size(max = 150)
    private String title;

    @Size(max = 2000)
    private String description;

    @Size(max = 200)
    private String location;

    private OffsetDateTime startTime;

    private OffsetDateTime endTime;

    private OffsetDateTime registrationDeadline;

    @Min(1)
    private Integer maxParticipants;

    private Boolean waitlistEnabled;

    private TrainingSessionStatus status;
}