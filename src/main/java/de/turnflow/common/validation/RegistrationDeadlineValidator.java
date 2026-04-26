package de.turnflow.common.validation;

import de.turnflow.session.dto.CreateTrainingSessionRequest;
import de.turnflow.session.dto.UpdateTrainingSessionRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.OffsetDateTime;

public class RegistrationDeadlineValidator
        implements ConstraintValidator<ValidRegistrationDeadline, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        OffsetDateTime startTime = null;
        OffsetDateTime registrationDeadline = null;

        if (value instanceof CreateTrainingSessionRequest request) {
            startTime = request.getStartTime();
            registrationDeadline = request.getRegistrationDeadline();
        }

        if (value instanceof UpdateTrainingSessionRequest request) {
            startTime = request.getStartTime();
            registrationDeadline = request.getRegistrationDeadline();
        }

        if (startTime == null || registrationDeadline == null) {
            return true;
        }

        return !registrationDeadline.isAfter(startTime);
    }
}