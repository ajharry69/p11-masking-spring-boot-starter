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
@EnableConfigurationProperties(P11MaskingProperties.class)
public class P11MaskingAutoConfiguration {

    @Bean
    public MaskingService maskingService(P11MaskingProperties properties) {
        return new MaskingService(properties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "p11.masking.json", name = "enabled", havingValue = "true")
    public JacksonModule maskingModule(MaskingService service, P11MaskingProperties props) {
        var module = new SimpleModule();
        // Registering a serializer for String means we intercept ALL strings,
        // but createContextual ensures we only ACT on specific fields.
        module.addSerializer(String.class, new MaskingSerializer(service, props));
        return module;
    }

    @Bean(initMethod = "initialize")
    @ConditionalOnClass(name = "ch.qos.logback.classic.LoggerContext")
    public MaskingLogbackInitializer maskingLogbackInitializer(MaskingService service, P11MaskingProperties props) {
        return new MaskingLogbackInitializer(service, props);
    }
}
