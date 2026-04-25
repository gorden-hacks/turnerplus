package de.turnflow.session.dto;


import de.turnflow.session.entity.TrainingSessionStatus;
import lombok.*;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingSessionCalendarDto {

    private Long id;

    private Long trainingGroupId;
    private String trainingGroupName;

    private String title;
    private String location;

    private OffsetDateTime startTime;
    private OffsetDateTime endTime;

    private TrainingSessionStatus status;

    private long registeredCount;
    private Integer maxParticipants;
}