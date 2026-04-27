package de.turnflow.session;

import de.turnflow.common.exception.BusinessException;
import de.turnflow.common.exception.ErrorCode;
import de.turnflow.registration.entity.RegistrationStatus;
import de.turnflow.session.entity.TrainingSession;
import de.turnflow.session.entity.TrainingSessionActionDisabledReason;
import de.turnflow.session.entity.TrainingSessionStatus;

import java.time.OffsetDateTime;

public record TrainingSessionActionDecision(boolean allowed, TrainingSessionActionDisabledReason reason) {

    public static TrainingSessionActionDecision allow() {
        return new TrainingSessionActionDecision(true, null);
    }

    public static TrainingSessionActionDecision deny(TrainingSessionActionDisabledReason reason) {
        return new TrainingSessionActionDecision(false, reason);
    }

    public static TrainingSessionActionDecision forRegister(
            TrainingSession session,
            boolean memberActive,
            boolean hasGroupPermission,
            RegistrationStatus currentRegistrationStatus,
            long registeredCount
    ) {
        OffsetDateTime now = OffsetDateTime.now();

        if (!memberActive) {
            return deny(TrainingSessionActionDisabledReason.MEMBER_INACTIVE);
        }

        if (!hasGroupPermission) {
            return deny(TrainingSessionActionDisabledReason.NOT_ALLOWED_FOR_GROUP);
        }

        if (session.getStatus() != TrainingSessionStatus.OPEN) {
            return deny(TrainingSessionActionDisabledReason.SESSION_NOT_OPEN);
        }

        if (session.getEndTime().isBefore(now)) {
            return deny(TrainingSessionActionDisabledReason.SESSION_IN_PAST);
        }

        if (session.getRegistrationDeadline() != null
                && session.getRegistrationDeadline().isBefore(now)) {
            return deny(TrainingSessionActionDisabledReason.REGISTRATION_DEADLINE_EXPIRED);
        }

        if (currentRegistrationStatus == RegistrationStatus.REGISTERED) {
            return deny(TrainingSessionActionDisabledReason.ALREADY_REGISTERED);
        }

        if (currentRegistrationStatus == RegistrationStatus.WAITLIST) {
            return deny(TrainingSessionActionDisabledReason.ALREADY_WAITLISTED);
        }

        if (session.getMaxParticipants() != null
                && registeredCount >= session.getMaxParticipants()
                && !session.isWaitlistEnabled()) {
            return deny(TrainingSessionActionDisabledReason.SESSION_FULL);
        }

        return allow();
    }

    public static TrainingSessionActionDecision forUnregister(
            TrainingSession session,
            RegistrationStatus currentRegistrationStatus
    ) {
        OffsetDateTime now = OffsetDateTime.now();

        if (currentRegistrationStatus == null) {
            return deny(TrainingSessionActionDisabledReason.NOT_REGISTERED);
        }

        if (currentRegistrationStatus == RegistrationStatus.CANCELLED) {
            return deny(TrainingSessionActionDisabledReason.ALREADY_CANCELLED);
        }

        if (session.getEndTime().isBefore(now)) {
            return deny(TrainingSessionActionDisabledReason.UNREGISTER_AFTER_SESSION_END_NOT_ALLOWED);
        }

        return allow();
    }

    public void throwIfDeniedForRegister() {
        if (!allowed) {
            throw new BusinessException(toErrorCode(reason));
        }
    }

    public void throwIfDeniedForUnregister() {
        if (!allowed) {
            throw new BusinessException(toErrorCode(reason));
        }
    }

    private static ErrorCode toErrorCode(TrainingSessionActionDisabledReason reason) {
        return switch (reason) {
            case MEMBER_INACTIVE -> ErrorCode.MEMBER_INACTIVE;
            case NOT_ALLOWED_FOR_GROUP -> ErrorCode.MEMBER_NOT_ALLOWED_FOR_GROUP;
            case SESSION_NOT_OPEN -> ErrorCode.TRAINING_SESSION_NOT_OPEN;
            case SESSION_IN_PAST -> ErrorCode.TRAINING_SESSION_IN_PAST;
            case REGISTRATION_DEADLINE_EXPIRED -> ErrorCode.REGISTRATION_DEADLINE_EXPIRED;
            case SESSION_FULL -> ErrorCode.TRAINING_SESSION_FULL;
            case ALREADY_REGISTERED -> ErrorCode.MEMBER_ALREADY_REGISTERED;
            case UNREGISTER_AFTER_SESSION_END_NOT_ALLOWED -> ErrorCode.UNREGISTER_AFTER_SESSION_END_NOT_ALLOWED;
            case NOT_REGISTERED -> ErrorCode.REGISTRATION_NOT_FOUND;
            case ALREADY_CANCELLED -> ErrorCode.REGISTRATION_ALREADY_CANCELLED;
            case ALREADY_WAITLISTED -> ErrorCode.MEMBER_ALREADY_WAITLISTED;
        };
    }
}