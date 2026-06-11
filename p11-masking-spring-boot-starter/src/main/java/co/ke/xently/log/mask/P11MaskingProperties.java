package co.ke.xently.log.mask;

import co.ke.xently.log.mask.utils.validators.ValidRegexList;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Validated
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "p11.masking")
public class P11MaskingProperties {
    @Builder.Default
    private boolean enabled = true;
    private List<String> fields;
    @ValidRegexList
    private List<String> patterns;
    @NonNull
    @NotNull
    @Builder.Default
    private MaskingStyle maskStyle = MaskingStyle.FULL;
    @NotEmpty
    @Builder.Default
    private String maskCharacter = "*";
    @Builder.Default
    private Json json = new Json();

    @NonNull
    public List<@NonNull String> getFields() {
        if (fields != null && !fields.isEmpty()) return fields;
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

    public @NonNull List<@NonNull String> getPatterns() {
        if (patterns == null) return Collections.emptyList();
        return patterns;
    }

    public boolean isFieldConfigured(String name) {
        if (name == null) return false;
        var effective = getFields();
        if (effective.isEmpty()) return false;
        var normalized = name.toLowerCase(Locale.ROOT);
        return effective.stream()
                .filter(value -> !value.isBlank())
                .map(value -> value.toLowerCase(Locale.ROOT))
                .anyMatch(value -> value.equals(normalized));
    }

    public boolean isJsonMaskingEnabled() {
        return json != null && json.isEnabled();
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Json {
        private boolean enabled = false;
    }

}