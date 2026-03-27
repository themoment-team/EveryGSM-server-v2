package team.themoment.everygsm.server.v2.global.client.datagsm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.RequestInterceptor;

@Configuration
public class DatagsmFeignConfig {

    @Value("${datagsm.api.key}")
    private String apiKey;

    @Bean
    public RequestInterceptor dataGsmRequestInterceptor() {
        return template -> template.header("X-API-KEY", apiKey);
    }
}