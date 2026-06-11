package co.ke.xently.log.mask.utils.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RegexListValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRegexList {
    
    String message() default "List contains one or more invalid regular expression patterns";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}