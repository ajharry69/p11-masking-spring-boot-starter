package co.ke.xently.log.mask;

import co.ke.xently.log.mask.utils.validators.ValidPartialMaskingExemption;
import co.ke.xently.log.mask.utils.validators.ValidRegexList;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Configuration properties for log masking and forging prevention.
 */
@NullMarked
@Validated
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "log")
public class LogProperties {
    /**
     * Configuration for log forging prevention.
     */
    @Builder.Default
    @Valid
    private Forging forging = new Forging();

    /**
     * Configuration for PII masking.
     */
    @Builder.Default
    @Valid
    private P11 p11 = new P11();

    /**
     * Settings for log forging prevention.
     */
    @Setter
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Forging {
        /**
         * The string used to replace illegal characters (newlines, tabs, etc.) in log messages.
         */
        @Builder.Default
        @Pattern(regexp = "[^\\t\\n\\r]+", message = "Log forging replacement must not contain tab (\\t), newline (\\n) or carriage return (\\r) characters")
        private String replacement = "_";
        /**
         * If true, continuous illegal characters are replaced with a single instance of the replacement string.
         */
        @Builder.Default
        private boolean replaceContinuousAtOnce = true;
    }

    /**
     * Settings for PII masking.
     */
    @Setter
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class P11 {
        /**
         * Configuration for PII masking.
         */
        @Builder.Default
        @Valid
        private Masking masking = new Masking();

        /**
         * Checks if a field name is configured for masking.
         *
         * @param name The field name to check.
         * @return True if the field is configured for masking, false otherwise.
         */
        public boolean isFieldConfigured(@Nullable String name) {
            if (name == null) return false;
            var effective = getMasking().getFields();
            if (effective.isEmpty()) return false;
            var normalized = name.toLowerCase(Locale.ROOT);
            return effective.stream()
                    .filter(value -> !value.isBlank())
                    .map(value -> value.toLowerCase(Locale.ROOT))
                    .anyMatch(value -> value.equals(normalized));
        }

        /**
         * Configuration for PII masking.
         */
        @Setter
        @Getter
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Masking {
            /**
             * Whether PII masking is enabled globally.
             */
            @Builder.Default
            private boolean enabled = true;
            /**
             * List of field names that should be automatically masked when found in log arguments or objects.
             */
            @Builder.Default
            private List<String> fields = Collections.emptyList();
            /**
             * List of regular expressions used to find sensitive information in log messages.
             * <p>
             * <b>Masking behavior:</b>
             * <ul>
             *     <li><b>With capturing groups:</b> Only the content of the capturing groups is masked. This is useful for masking values while keeping the surrounding context (e.g., {@code "token": "..."}) visible.</li>
             *     <li><b>Without capturing groups:</b> The entire match is masked.</li>
             * </ul>
             * <p>
             * <b>Example:</b>
             * <pre>{@code
             * # Mask only the value of the token parameter
             * patterns:
             *   - '(?:[a-z]+_)*token(?:_[a-z]+)*\s*=([^"]+)[])]'
             *   - '"(?:[a-z]+_)*token(?:_[a-z]+)*"\s*:\s*"([^"]+)"'
             * }</pre>
             */
            @ValidRegexList
            @Builder.Default
            private List<String> patterns = Collections.emptyList();
            /**
             * The default masking style to use.
             */
            @NotNull
            @Builder.Default
            private MaskingStyle maskStyle = MaskingStyle.FULL;
            /**
             * The character used for masking.
             */
            @Pattern(regexp = "\\S+", message = "Space character is not allowed in masking character")
            @Length(min = 1, max = 1)
            @Builder.Default
            private String maskCharacter = "*";
            /**
             * The default number of mask characters to show when masking a value.
             */
            @Min(value = 3)
            @Builder.Default
            private int defaultMaskLength = 8;
            /**
             * Configuration for partial masking exemptions.
             */
            @NotNull
            @Valid
            @ValidPartialMaskingExemption
            @Builder.Default
            private PartialMaskingExemption partialExemption = new PartialMaskingExemption();

            /**
             * Returns the list of fields to mask, or a default set if none are configured.
             *
             * @return The list of fields to mask.
             */
            public List<String> getFields() {
                if (!fields.isEmpty()) return fields;
                return List.of(
                        "password",
                        "passcode",
                        "secret",
                        "token",
                        "accessToken",
                        "refreshToken",
                        "ssn",
                        "creditCard",
                        "cardNumber",
                        "email",
                        "phone",
                        "phoneNumber",
                        "accountNumber",
                        "pin"
                );
            }

            /**
             * Configuration for partial masking exemptions.
             */
            @Setter
            @Getter
            @Builder
            @AllArgsConstructor
            @NoArgsConstructor
            public static class PartialMaskingExemption {
                /**
                 * Number of characters to leave unmasked at the start of the string.
                 */
                @PositiveOrZero
                @Builder.Default
                private int fromStart = 1;
                /**
                 * Number of characters to leave unmasked at the end of the string.
                 */
                @PositiveOrZero
                @Builder.Default
                private int fromEnd = 0;
                /**
                 * Minimum number of unmasked characters required to apply partial masking.
                 */
                @Positive
                @Builder.Default
                private int minUnmaskedLength = 3;
                /**
                 * Whether to mask the entire string ({@code MaskingStyle.FULL} style) if it is too short for the configured exemption.
                 */
                @Builder.Default
                private boolean maskIfShort = true;
            }
        }
    }
}