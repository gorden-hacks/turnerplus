package de.turnflow.session.dto;


import de.turnflow.common.validation.ValidDateRange;
import de.turnflow.session.entity.TrainingSessionStatus;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@ValidDateRange(from = "startTime", to = "endTime")
public class UpdateTrainingSessionRequest {

    private Long trainingGroupId;

    private String title;

    private String description;

    private String location;

    private OffsetDateTime startTime;

    private OffsetDateTime endTime;

    private OffsetDateTime registrationDeadline;

    private Integer maxParticipants;

    private Boolean waitlistEnabled;

    private TrainingSessionStatus status;
}