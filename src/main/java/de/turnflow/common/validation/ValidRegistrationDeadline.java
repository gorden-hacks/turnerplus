package de.turnflow.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RegistrationDeadlineValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRegistrationDeadline {

    String message() default "{validation.registrationDeadline.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}