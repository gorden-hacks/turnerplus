package de.turnflow.common.validation;

import de.turnflow.session.dto.IsoWeekRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.IsoFields;

public class IsoWeekValidator implements ConstraintValidator<ValidIsoWeek, IsoWeekRequest> {

    @Override
    public boolean isValid(IsoWeekRequest value, ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }

        int year = value.getYear();
        int week = value.getWeek();

        if (week < 1) {
            return false;
        }

        try {
            // Prüft, ob Woche im Jahr existiert
            LocalDate.of(year, 1, 4)
                    .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week);
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}