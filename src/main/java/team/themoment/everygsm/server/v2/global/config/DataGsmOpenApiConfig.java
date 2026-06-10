package team.themoment.everygsm.server.v2.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import team.themoment.datagsm.sdk.openapi.DataGsmOpenApiClient;

@Configuration
public class DataGsmOpenApiConfig {

    @Bean
    public DataGsmOpenApiClient dataGsmOpenApiClient(@Value("${spring.cloud.datagsm.openapi.api-key}") String apiKey,
            @Value("${spring.cloud.datagsm.openapi.base-url}") String baseUrl) {
        return DataGsmOpenApiClient.builder(apiKey).baseUrl(baseUrl).build();
    }
}
