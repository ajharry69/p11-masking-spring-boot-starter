package co.ke.xently.log.mask.utils.validators;

import co.ke.xently.log.mask.LogProperties.P11.Masking.PartialMaskingExemption;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class PartialMaskingExemptionValidatorTest {

    static Stream<PartialMaskingExemption> shouldFlagAsValid() {
        return Stream.of(
                null,
                new PartialMaskingExemption(0, 1, 4, true),
                new PartialMaskingExemption(1, 0, 4, true),
                new PartialMaskingExemption(1, 1, 4, true)
        );
    }

    static Stream<PartialMaskingExemption> shouldFlagAsInvalid() {
        return Stream.of(
                new PartialMaskingExemption(0, 0, 4, true),
                new PartialMaskingExemption(-1, -1, 4, true),
                new PartialMaskingExemption(0, -1, 4, true),
                new PartialMaskingExemption(-1, 0, 4, true)
        );
    }

    @ParameterizedTest
    @MethodSource
    void shouldFlagAsValid(PartialMaskingExemption exemption) {
        var validator = new PartialMaskingExemptionValidator();
        var valid = validator.isValid(exemption, null);
        assertThat(valid).isEqualTo(true);
    }

    @ParameterizedTest
    @MethodSource
    void shouldFlagAsInvalid(PartialMaskingExemption exemption) {
        var validator = new PartialMaskingExemptionValidator();
        var valid = validator.isValid(exemption, null);
        assertThat(valid).isEqualTo(false);
    }
}