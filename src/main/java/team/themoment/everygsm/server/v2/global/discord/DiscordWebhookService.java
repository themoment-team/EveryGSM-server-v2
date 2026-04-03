package team.themoment.everygsm.server.v2.global.discord;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import team.themoment.everygsm.server.v2.thirdparty.feign.discord.DiscordWebhookClient;
import team.themoment.everygsm.server.v2.thirdparty.feign.discord.dto.DiscordWebhookPayload;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordWebhookService {

    private final DiscordWebhookClient discordWebhookClient;

    @Async
    public void sendServerError(String title, String description) {
        try {
            discordWebhookClient.send(DiscordWebhookPayload.serverError(title, description));
        } catch (Exception e) {
            log.warn("[DISCORD-WEBHOOK] 알림 전송 실패: {}", e.getMessage());
        }
    }
}
