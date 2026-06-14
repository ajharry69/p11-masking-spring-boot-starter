package co.ke.xently.log.mask;

import co.ke.xently.log.mask.LogProperties.P11.Masking.PartialMaskingExemption;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class LogMaskingService {
    private static final Map<String, String> MASK_SEGMENT_CACHE = new HashMap<>();
    private final LogProperties properties;
    private String maskSegment = null;

    public LogMaskingService(LogProperties properties) {
        this.properties = properties;
    }

    public String mask(String input) {
        return mask(input, null, null);
    }

    public String mask(String input, MaskingStyle styleOverride, String maskCharacterOverride) {
        var masking = properties.getP11().getMasking();
        if (!masking.isEnabled() || !StringUtils.hasText(input)) return input;

        final var ch = resolveMaskCharacter(maskCharacterOverride);
        maskSegment = MASK_SEGMENT_CACHE.computeIfAbsent(ch, k -> k.repeat(masking.getDefaultMaskLength()));
        final var inputLength = input.length();
        return switch (resolveStyle(styleOverride)) {
            case FULL, DEFAULT -> maskSegment;
            case PARTIAL -> {
                var maskingExemption = masking.getPartialExemption();

                if (input.contains("@")) { // Email
                    var usernameLength = input.indexOf("@");
                    var username = input.substring(0, usernameLength);
                    yield getPartialMask(username, usernameLength, maskingExemption) + input.substring(usernameLength);
                }
                yield getPartialMask(input, inputLength, maskingExemption);
            }
        };
    }

    private String getPartialMask(String input, int inputLength, PartialMaskingExemption maskingExemption) {
        var fromStart = maskingExemption.getFromStart();
        var fromEnd = maskingExemption.getFromEnd();
        if (inputLength - fromStart - fromEnd <= maskingExemption.getMinUnmaskedLength()) {
            return maskingExemption.isMaskIfShort() ? maskSegment : input;
        }
        return input.substring(0, fromStart) + maskSegment + input.substring(inputLength - fromEnd);
    }

    private MaskingStyle resolveStyle(MaskingStyle styleOverride) {
        var override = styleOverride == null || styleOverride == MaskingStyle.DEFAULT;
        var resolved = override ? properties.getP11().getMasking().getMaskStyle() : styleOverride;
        return resolved == MaskingStyle.DEFAULT
                ? MaskingStyle.FULL
                : resolved;
    }

    private String resolveMaskCharacter(String maskCharacterOverride) {
        if (StringUtils.hasText(maskCharacterOverride)) return maskCharacterOverride;
        return properties.getP11().getMasking().getMaskCharacter();
    }
}
