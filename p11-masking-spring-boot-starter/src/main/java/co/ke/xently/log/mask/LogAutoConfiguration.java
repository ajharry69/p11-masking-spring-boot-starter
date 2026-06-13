package co.ke.xently.log.mask;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.ObjectMapper;

@AutoConfiguration
@ConditionalOnClass(ObjectMapper.class)
@EnableConfigurationProperties(LogProperties.class)
public class LogAutoConfiguration {

    @Bean
    public LogForgingService logForgingService(LogProperties properties) {
        return new LogForgingService(properties);
    }

    @Bean
    public MaskingService maskingService(LogProperties properties) {
        return new MaskingService(properties);
    }

    @Bean(initMethod = "initialize")
    @ConditionalOnClass(name = "ch.qos.logback.classic.LoggerContext")
    public MaskingLogbackInitializer maskingLogbackInitializer(MaskingService service, LogForgingService forgingService, LogProperties props) {
        return new MaskingLogbackInitializer(service, forgingService, props);
    }
}
