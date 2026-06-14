package co.ke.xently.log.mask;

import co.ke.xently.log.mask.utils.validators.ValidPartialMaskingExemption;
import co.ke.xently.log.mask.utils.validators.ValidRegexList;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

@NullMarked
@Validated
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "log")
public class LogProperties {
    @Builder.Default
    @Valid
    private Forging forging = new Forging();

    @Builder.Default
    @Valid
    private P11 p11 = new P11();

    @Setter
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Forging {
        @Builder.Default
        @Pattern(regexp = "[^\\t\\n\\r]+", message = "Log forging replacement must not contain tab (\\t), newline (\\n) or carriage return (\\r) characters")
        private String replacement = "_";
        @Builder.Default
        private boolean replaceContinuousAtOnce = true;
    }

    @Setter
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class P11 {
        @Builder.Default
        @Valid
        private Masking masking = new Masking();

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

        @Setter
        @Getter
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Masking {
            @Builder.Default
            private boolean enabled = true;
            @Builder.Default
            private List<String> fields = Collections.emptyList();
            @ValidRegexList
            @Builder.Default
            private List<String> patterns = Collections.emptyList();
            @NotNull
            @Builder.Default
            private MaskingStyle maskStyle = MaskingStyle.FULL;
            @NotEmpty
            @Builder.Default
            private String maskCharacter = "*";
            @Min(value = 3)
            @Builder.Default
            private int defaultMaskLength = 8;
            @NotNull
            @Valid
            @ValidPartialMaskingExemption
            @Builder.Default
            private PartialMaskingExemption partialMaskingExemption = new PartialMaskingExemption();

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

            @Setter
            @Getter
            @Builder
            @AllArgsConstructor
            @NoArgsConstructor
            public static class PartialMaskingExemption {
                @PositiveOrZero
                @Builder.Default
                private int fromStart = 1;
                @PositiveOrZero
                @Builder.Default
                private int fromEnd = 0;
                @Positive
                @Builder.Default
                private int minPartialUnmaskedLength = 3;
                @Builder.Default
                private boolean maskIfShortOrEqualToExemption = true;
            }
        }
    }
}