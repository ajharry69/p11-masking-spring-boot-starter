package co.ke.xently.log.mask;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.module.SimpleModule;

@AutoConfiguration
@ConditionalOnClass(ObjectMapper.class)
@EnableConfigurationProperties(LogProperties.class)
public class LogAutoConfiguration {

    @Bean
    public MaskingService maskingService(LogProperties properties) {
        return new MaskingService(properties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "log.p11.masking.json", name = "enabled", havingValue = "true")
    public JacksonModule maskingModule(MaskingService service, LogProperties props) {
        var module = new SimpleModule();
        // Registering a serializer for String means we intercept ALL strings,
        // but createContextual ensures we only ACT on specific fields.
        module.addSerializer(String.class, new MaskingSerializer(service, props));
        return module;
    }

    @Bean(initMethod = "initialize")
    @ConditionalOnClass(name = "ch.qos.logback.classic.LoggerContext")
    public MaskingLogbackInitializer maskingLogbackInitializer(MaskingService service, LogProperties props) {
        return new MaskingLogbackInitializer(service, props);
    }
}
