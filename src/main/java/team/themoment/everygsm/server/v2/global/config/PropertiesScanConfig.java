package team.themoment.everygsm.server.v2.global.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationPropertiesScan(basePackages = {"team.themoment.everygsm.server.v2.global.security.data"})
public class PropertiesScanConfig {
}
