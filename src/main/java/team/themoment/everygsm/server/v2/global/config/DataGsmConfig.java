package team.themoment.everygsm.server.v2.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import team.themoment.datagsm.sdk.oauth.DataGsmClient;

@Configuration
public class DataGsmConfig {
    @Bean
    public DataGsmClient dataGsmClient(@Value("${oauth.datagsm.client-secret}") String clientSecret) {
        return DataGsmClient.builder(clientSecret).build();
    }
}
