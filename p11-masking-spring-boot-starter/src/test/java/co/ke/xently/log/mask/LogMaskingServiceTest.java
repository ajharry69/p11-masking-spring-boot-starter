package co.ke.xently.log.mask;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class LogMaskingServiceTest {

    static Stream<String> shouldHandleNullAndEmptySafely() {
        return Stream.of(null, "");
    }

    @ParameterizedTest
    @MethodSource
    void shouldHandleNullAndEmptySafely(String input) {
        var props = LogProperties.builder()
                .p11(LogProperties.P11.builder()
                        .masking(LogProperties.P11.Masking.builder()
                                .maskStyle(MaskingStyle.PARTIAL)
                                .maskCharacter("*")
                                .build())
                        .build())
                .build();
        var service = new LogMaskingService(props);

        var actual = service.mask(input);

        assertThat(actual, equalTo(input));
    }

    @Nested
    class PartialMasking {
        @ParameterizedTest(name = "shouldPartiallyMaskInputs: {0}; fromStart: {1}; fromEnd: {2} -> {3}")
        @CsvSource({
                "john.doe@example.com, 1, 0, j********@example.com",
                "john.doe@example.com, 1, 1, j********e@example.com",
                "a@b.com, 1, 0, ********@b.com",
                "0712345678, 1, 0, 0********",
                "1234567890, 0, 4, ********7890",
                "1234567890, 2, 4, 12********7890",
                "1234567890, 1, 4, 1********7890",
                "1234567890, 5, 4, ********", // unmasked would be too short
                "1234567890, 5, 5, ********", // masked section would actually be empty
                "4111111111111111, 0, 4, ********1111",
                "123, 0, 4, ********", // < 4
                "1234, 0, 4, ********"
        })
        void shouldPartiallyMaskInputs(String input, int fromStart, int fromEnd, String expected) {
            var props = LogProperties.builder()
                    .p11(LogProperties.P11.builder()
                            .masking(LogProperties.P11.Masking.builder()
                                    .maskStyle(MaskingStyle.PARTIAL)
                                    .maskCharacter("*")
                                    .partialExemption(LogProperties.P11.Masking.PartialMaskingExemption.builder()
                                            .fromStart(fromStart)
                                            .fromEnd(fromEnd)
                                            .build())
                                    .build())
                            .build())
                    .build();
            var service = new LogMaskingService(props);

            assertThat(service.mask(input), equalTo(expected));
        }
    }

    @Nested
    class FullMasking {
        @ParameterizedTest(name = "shouldFullyMask: {0}")
        @CsvSource({
                "short, ********",
                "mediumLength, ********",
                "thisIsAVeryLongStringThatShouldStillBeMaskedToEightCharacters, ********"
        })
        void shouldFullyMaskWithFixedLength(String input, String expected) {
            var props = LogProperties.builder()
                    .p11(LogProperties.P11.builder()
                            .masking(LogProperties.P11.Masking.builder()
                                    .maskStyle(MaskingStyle.FULL)
                                    .maskCharacter("*")
                                    .build())
                            .build())
                    .build();
            var service = new LogMaskingService(props);

            var actual = service.mask(input);

            assertThat(actual, equalTo(expected));
        }
    }

    @Nested
    class Overrides {
        @ParameterizedTest(name = "shouldOverrideStyleAndChar: {0} -> {1}")
        @CsvSource({
                "1234567890, 1########",
                "1234, ########"
        })
        void shouldOverrideStyleAndMaskCharacter(String input, String expected) {
            var props = LogProperties.builder()
                    .p11(LogProperties.P11.builder()
                            .masking(LogProperties.P11.Masking.builder()
                                    .maskStyle(MaskingStyle.PARTIAL)
                                    .maskCharacter("*")
                                    .build())
                            .build())
                    .build();
            var service = new LogMaskingService(props);

            var actual = service.mask(input, MaskingStyle.PARTIAL, "#");

            assertThat(actual, equalTo(expected));
        }
    }
}
