package team.themoment.everygsm.server.v2.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import team.themoment.datagsm.sdk.oauth.DataGsmOAuthClient;

@Configuration
public class DataGsmOAuthConfig {
    @Bean
    public DataGsmOAuthClient dataGsmOAuthClient(@Value("${oauth.datagsm.client-id}") String clientId,
            @Value("${oauth.datagsm.client-secret}") String clientSecret) {
        return DataGsmOAuthClient.builder(clientId, clientSecret)
                .authorizationBaseUrl("https://oauth-auth-data.stage.hellogsm.kr")
                .userInfoBaseUrl("https://oauth-userinfo-data.stage.hellogsm.kr").build();
    }
}
