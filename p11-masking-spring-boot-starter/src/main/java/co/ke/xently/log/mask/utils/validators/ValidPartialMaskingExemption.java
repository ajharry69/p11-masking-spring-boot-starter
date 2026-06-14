package co.ke.xently.log.mask.utils.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PartialMaskingExemptionValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPartialMaskingExemption {

    String message() default "{validation.partialMaskingExemption.fromStartOrFromEnd.required}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}