package com.github.ajharry69.log.mask;

import lombok.AllArgsConstructor;
import org.springframework.util.StringUtils;

@AllArgsConstructor
public class MaskingService {
    private final P11MaskingProperties properties;

    public String mask(String input) {
        if (!properties.isEnabled() || !StringUtils.hasText(input)) return input;

        var ch = properties.getMaskCharacter();
        return switch (properties.getMaskStyle()) {
            case FULL -> ch.repeat(8);
            case LAST4 -> {
                if (input.length() <= 4) yield ch.repeat(input.length());
                yield ch.repeat(input.length() - 4) + input.substring(input.length() - 4);
            }
            case PARTIAL -> {
                if (input.contains("@")) { // Email
                    int atIndex = input.indexOf("@");
                    if (atIndex <= 1) yield input; // Too short to mask
                    yield input.charAt(0) + ch.repeat(atIndex - 1) + input.substring(atIndex);
                }
                // Default partial (keep first char)
                if (input.length() <= 1) yield input;
                yield input.charAt(0) + ch.repeat(input.length() - 1);
            }
        };
    }
}