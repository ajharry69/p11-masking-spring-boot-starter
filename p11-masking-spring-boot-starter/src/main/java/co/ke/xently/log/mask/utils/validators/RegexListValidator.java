package co.ke.xently.log.mask.utils.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegexListValidator implements ConstraintValidator<ValidRegexList, List<String>> {

    @Override
    public boolean isValid(List<String> values, ConstraintValidatorContext context) {
        if (values == null) {
            return true;
        }

        List<String> invalidPatterns = new ArrayList<>();

        for (int i = 0; i < values.size(); i++) {
            var pattern = values.get(i);

            if (pattern == null) {
                invalidPatterns.add("null (at index " + i + ")");
                continue;
            }

            try {
                Pattern.compile(pattern);
            } catch (PatternSyntaxException e) {
                invalidPatterns.add("'" + pattern + "'");
            }
        }

        if (invalidPatterns.isEmpty()) return true;

        context.disableDefaultConstraintViolation();

        var hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class)
                .addMessageParameter("invalidPatterns", String.join(", ", invalidPatterns));
        hibernateContext.buildConstraintViolationWithTemplate("List contains invalid regular expression patterns: [{invalidPatterns}]")
                .addConstraintViolation();

        return false;

    }
}