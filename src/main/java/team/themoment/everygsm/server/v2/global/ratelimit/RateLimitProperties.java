package team.themoment.everygsm.server.v2.global.ratelimit;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rate-limit")
public record RateLimitProperties(int maxRequests, long windowSeconds) {

    public RateLimitProperties {
        if (maxRequests <= 0)
            maxRequests = 100;
        if (windowSeconds <= 0)
            windowSeconds = 60;
    }
}
