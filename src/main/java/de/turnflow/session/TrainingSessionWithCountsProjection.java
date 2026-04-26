package de.turnflow.session;

import de.turnflow.session.entity.TrainingSessionStatus;

import java.time.OffsetDateTime;

public interface TrainingSessionWithCountsProjection {

    Long getId();

    Long getTrainingGroupId();

    String getTrainingGroupName();

    String getTitle();

    String getDescription();

    String getLocation();

    OffsetDateTime getStartTime();

    OffsetDateTime getEndTime();

    OffsetDateTime getRegistrationDeadline();

    Integer getMaxParticipants();

    Boolean getWaitlistEnabled();

    TrainingSessionStatus getStatus();

    Long getRegisteredCount();

    Long getWaitlistCount();
}