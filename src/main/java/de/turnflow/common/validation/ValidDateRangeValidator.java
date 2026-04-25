package de.turnflow.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

import java.time.LocalDate;

public class ValidDateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {

    private String fromField;
    private String toField;

    @Override
    public void initialize(ValidDateRange annotation) {
        this.fromField = annotation.from();
        this.toField = annotation.to();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        LocalDate from = (LocalDate) new BeanWrapperImpl(value).getPropertyValue(fromField);
        LocalDate to = (LocalDate) new BeanWrapperImpl(value).getPropertyValue(toField);

        if (from == null || to == null) {
            return true;
        }

        return !to.isBefore(from);
    }
}