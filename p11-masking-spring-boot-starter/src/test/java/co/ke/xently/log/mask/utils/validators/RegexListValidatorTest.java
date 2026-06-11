package co.ke.xently.log.mask.utils.validators;

import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.constraintvalidation.HibernateConstraintViolationBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegexListValidatorTest {

    private RegexListValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private HibernateConstraintValidatorContext hibernateContext;

    @Mock
    private HibernateConstraintViolationBuilder violationBuilder;

    @BeforeEach
    void setUp() {
        validator = new RegexListValidator();
    }

    @Test
    void shouldReturnTrueWhenListIsNull() {
        boolean valid = validator.isValid(null, context);
        assertThat(valid, is(true));
        verifyNoInteractions(context);
    }

    @Test
    void shouldReturnTrueWhenListIsEmpty() {
        boolean valid = validator.isValid(Collections.emptyList(), context);
        assertThat(valid, is(true));
        verifyNoInteractions(context);
    }

    static Stream<List<String>> validRegexLists() {
        return Stream.of(
                Collections.singletonList("^[a-z]+$"),
                Arrays.asList("^[a-z]+$", "\\d+"),
                Arrays.asList(".*", "foo|bar")
        );
    }

    @ParameterizedTest
    @MethodSource("validRegexLists")
    void shouldReturnTrueWhenAllPatternsAreValid(List<String> values) {
        boolean valid = validator.isValid(values, context);
        assertThat(valid, is(true));
        verifyNoInteractions(context);
    }

    static Stream<Arguments> invalidRegexLists() {
        return Stream.of(
                Arguments.of(
                        Collections.singletonList(null),
                        "null (at index 0)"
                ),
                Arguments.of(
                        Arrays.asList("^[a-z]+$", null),
                        "null (at index 1)"
                ),
                Arguments.of(
                        Collections.singletonList("["),
                        "'['"
                ),
                Arguments.of(
                        Arrays.asList("[", "*"),
                        "'[', '*'"
                ),
                Arguments.of(
                        Arrays.asList("valid", "[", null),
                        "'[', null (at index 2)"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("invalidRegexLists")
    void shouldReturnFalseWhenPatternsAreInvalid(List<String> values, String expectedInvalidPatterns) {
        when(context.unwrap(HibernateConstraintValidatorContext.class)).thenReturn(hibernateContext);
        when(hibernateContext.addMessageParameter(anyString(), anyString())).thenReturn(hibernateContext);
        when(hibernateContext.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);

        boolean valid = validator.isValid(values, context);

        assertThat(valid, is(false));

        verify(context).disableDefaultConstraintViolation();
        verify(context).unwrap(HibernateConstraintValidatorContext.class);
        verify(hibernateContext).addMessageParameter("invalidPatterns", expectedInvalidPatterns);
        verify(hibernateContext).buildConstraintViolationWithTemplate(
                "List contains invalid regular expression patterns: [{invalidPatterns}]");
        verify(violationBuilder).addConstraintViolation();
    }
}