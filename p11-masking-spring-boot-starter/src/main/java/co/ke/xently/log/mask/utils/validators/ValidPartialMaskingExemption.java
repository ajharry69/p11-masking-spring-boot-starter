package co.ke.xently.log.mask.utils.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PartialMaskingExemptionValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPartialMaskingExemption {

    String message() default "At lease one of `fromStart` or `fromEnd` must be greater than 0";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}