package co.ke.xently.log.mask;

import co.ke.xently.log.mask.utils.LogSanitizer;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;

@RequiredArgsConstructor
public class LogForgingService {
    private final LogProperties properties;

    public void process(LinkedHashMap<String, String> replacements, Object value) {
        var raw = String.valueOf(value);
        var sanitized = LogSanitizer.sanitize(
                value,
                properties.getForging().getReplacement(),
                properties.getForging().isReplaceContinuousAtOnce()
        );
        if (!raw.equals(sanitized)) {
            replacements.put(raw, sanitized);
        }
    }
}
