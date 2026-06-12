package co.ke.xently.log.mask.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class LogSanitizerTest {
    static Stream<Arguments> valuesToSanitize() {
        return Stream.of(
                Arguments.of("RRN123-safe value", "RRN123-safe value"),
                Arguments.of("RRN123\nspoofed", "RRN123_spoofed"),
                Arguments.of("RRN123\nINFO forged audit entry", "RRN123_INFO forged audit entry"),
                Arguments.of("RRN123\rINFO forged audit entry", "RRN123_INFO forged audit entry"),
                Arguments.of("RRN123\r\nINFO forged audit entry\nspoofed", "RRN123__INFO forged audit entry_spoofed"),
                Arguments.of("\t\n\r\r\n\t", "______")
        );
    }

    @Test
    void shouldReturnNullWhenValueIsNull() {
        assertThat(LogSanitizer.sanitize(null))
                .isNull();
    }

    @ParameterizedTest
    @MethodSource("valuesToSanitize")
    void shouldSanitizeValues(String value, String expected) {
        assertThat(LogSanitizer.sanitize(value))
                .isEqualTo(expected);
    }

    @Test
    void shouldSanitizeObjectStringRepresentation() {
        var value = new Object() {
            @Override
            public String toString() {
                return "object\r\nvalue\tspoofed";
            }
        };
        assertThat(LogSanitizer.sanitize(value))
                .isEqualTo("object__value_spoofed");

    }
}