package co.ke.xently.log.mask.utils;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class LogSanitizer {
    private static final Map<String, String> CACHE = new HashMap<>();

    public String sanitize(Object value) {
        return sanitize(value, "_", false);
    }

    public String sanitize(Object value, String replacement, boolean replaceContinuousAtOnce) {
        if (value == null) {
            return null;
        }
        return CACHE.computeIfAbsent(value.toString(), s -> s.replaceAll(replaceContinuousAtOnce ? "[\\t\\n\\r]+" : "[\\t\\n\\r]", replacement));
    }
}
