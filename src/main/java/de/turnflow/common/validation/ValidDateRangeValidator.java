package de.turnflow.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

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
        Object fromObj = new BeanWrapperImpl(value).getPropertyValue(fromField);
        Object toObj = new BeanWrapperImpl(value).getPropertyValue(toField);

        if (fromObj == null || toObj == null) {
            return true;
        }

        if (fromObj instanceof Comparable && toObj instanceof Comparable) {
            Comparable from = (Comparable) fromObj;
            Comparable to = (Comparable) toObj;

            return to.compareTo(from) >= 0;
        }

        return true;
    }
}