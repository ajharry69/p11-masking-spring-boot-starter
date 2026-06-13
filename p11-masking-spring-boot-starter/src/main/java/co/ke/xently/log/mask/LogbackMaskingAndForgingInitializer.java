package co.ke.xently.log.mask;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.OutputStreamAppender;
import lombok.AllArgsConstructor;
import org.slf4j.LoggerFactory;

import java.util.IdentityHashMap;
import java.util.function.Supplier;

@AllArgsConstructor
public class LogbackMaskingAndForgingInitializer implements Supplier<LogMaskingAndForgingConverter> {
    private final LogMaskingService service;
    private final LogForgingService forgingService;
    private final LogProperties properties;

    @Override
    public LogMaskingAndForgingConverter get() {
        return new LogMaskingAndForgingConverter(service, forgingService, properties);
    }

    public void initialize() {
        var loggerFactory = LoggerFactory.getILoggerFactory();
        if (loggerFactory instanceof LoggerContext context) {
            registerConverter("m");
            registerConverter("msg");
            registerConverter("message");
            restartPatternEncoders(context);
        }
    }

    private void registerConverter(String key) {
        try {
            PatternLayout.DEFAULT_CONVERTER_SUPPLIER_MAP.put(key, this::get);
        } catch (UnsupportedOperationException ignored) {
            // Some logback versions return unmodifiable maps.
        }
    }

    private void restartPatternEncoders(LoggerContext context) {
        var seen = new IdentityHashMap<Appender<?>, Boolean>();
        for (var logger : context.getLoggerList()) {
            var iterator = logger.iteratorForAppenders();
            while (iterator.hasNext()) {
                var appender = iterator.next();
                if (seen.put(appender, true) != null) continue;
                if (appender instanceof OutputStreamAppender<?> streamAppender) {
                    var encoder = streamAppender.getEncoder();
                    if (encoder instanceof PatternLayoutEncoder patternEncoder) {
                        var layout = patternEncoder.getLayout();
                        if (layout instanceof PatternLayout patternLayout) {
                            var instanceMap = patternLayout.getInstanceConverterMap();
                            instanceMap.put("m", this::get);
                            instanceMap.put("msg", this::get);
                            instanceMap.put("message", this::get);
                        }
                        patternEncoder.stop();
                        patternEncoder.start();
                    }
                }
            }
        }
    }
}
