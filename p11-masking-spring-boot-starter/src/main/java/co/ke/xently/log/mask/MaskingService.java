package co.ke.xently.log.mask;

import lombok.AllArgsConstructor;
import org.springframework.util.StringUtils;

@AllArgsConstructor
public class MaskingService {
    private final P11MaskingProperties properties;
    private static final int DEFAULT_MASK_LENGTH = 8;

    public String mask(String input) {
        return mask(input, null, null);
    }

    public String mask(String input, MaskingStyle styleOverride, String maskCharacterOverride) {
        if (!properties.isEnabled() || !StringUtils.hasText(input)) return input;

        final var ch = resolveMaskCharacter(maskCharacterOverride);
        final var maskSegment = ch.repeat(DEFAULT_MASK_LENGTH);
        return switch (resolveStyle(styleOverride)) {
            case FULL, DEFAULT -> maskSegment;
            case LAST4 -> {
                int unmaskedCharacters = 4;
                if (input.length() <= unmaskedCharacters) yield maskSegment;
                yield maskSegment + input.substring(input.length() - unmaskedCharacters);
            }
            case PARTIAL -> {
                if (input.contains("@")) { // Email
                    int atIndex = input.indexOf("@");
                    if (atIndex <= 1) yield input; // Too short to mask
                    yield input.charAt(0) + maskSegment + input.substring(atIndex);
                }
                // Default partial (keep first char)
                if (input.length() <= 1) yield input;
                yield input.charAt(0) + maskSegment;
            }
        };
    }

    private MaskingStyle resolveStyle(MaskingStyle styleOverride) {
        var override = styleOverride == null || styleOverride == MaskingStyle.DEFAULT;
        var resolved = override ? properties.getMaskStyle() : styleOverride;
        return resolved == MaskingStyle.DEFAULT
                ? MaskingStyle.FULL
                : resolved;
    }

    private String resolveMaskCharacter(String maskCharacterOverride) {
        if (StringUtils.hasText(maskCharacterOverride)) return maskCharacterOverride;
        return StringUtils.hasText(properties.getMaskCharacter()) ? properties.getMaskCharacter() : "*";
    }
}
