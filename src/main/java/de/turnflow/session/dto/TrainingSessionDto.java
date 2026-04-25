package de.turnflow.session.dto;

import de.turnflow.session.entity.TrainingSessionStatus;
import lombok.*;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingSessionDto {

    private Long id;

    private Long trainingGroupId;
    private String trainingGroupName;

    private String title;
    private String description;
    private String location;

    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private OffsetDateTime registrationDeadline;

    private Integer maxParticipants;
    private boolean waitlistEnabled;

    private TrainingSessionStatus status;

    private long registeredCount;
    private long waitlistCount;
}