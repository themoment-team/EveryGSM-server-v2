package team.themoment.everygsm.server.v2.global.discord;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "discordWebhook", url = "${discord.webhook.url}")
public interface DiscordWebhookClient {

    @PostMapping
    void send(@RequestBody DiscordWebhookPayload payload);
}
