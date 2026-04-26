package de.turnflow.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IsoWeekValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidIsoWeek {

    String message() default "{validation.isoWeek.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}