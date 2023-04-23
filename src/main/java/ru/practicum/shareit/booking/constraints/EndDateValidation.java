package ru.practicum.shareit.booking.constraints;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = BookingEndDateValidator.class)
@Target({TYPE})
@Retention(RUNTIME)
@Documented
public @interface EndDateValidation {

  String message() default "EndDate must be after startDate";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
