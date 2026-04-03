package team.themoment.everygsm.server.v2.global.thirdparty.feign.discord;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import team.themoment.everygsm.server.v2.global.thirdparty.feign.discord.dto.DiscordWebhookPayload;

@FeignClient(name = "discordWebhook", url = "${spring.cloud.discord.webhook.url}")
public interface DiscordWebhookClient {

    @PostMapping
    void send(@RequestBody DiscordWebhookPayload payload);
}
