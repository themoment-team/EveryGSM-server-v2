package team.themoment.everygsm.server.v2.global.discord;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import team.themoment.everygsm.server.v2.global.thirdparty.feign.discord.DiscordWebhookClient;
import team.themoment.everygsm.server.v2.global.thirdparty.feign.discord.dto.DiscordWebhookPayload;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordWebhookService {

    private final DiscordWebhookClient discordWebhookClient;

    // 서블릿 스코프가 끝나기 전에 요청 정보를 미리 추출해서 전달받음
    @Async
    public void sendServerError(String title,
            String description,
            String httpMethod,
            String requestUri,
            Throwable cause) {
        try {
            String detail = buildDetail(description, httpMethod, requestUri, cause);
            discordWebhookClient.send(DiscordWebhookPayload.serverError(title, detail));
        } catch (Exception e) {
            log.warn("[DISCORD-WEBHOOK] 알림 전송 실패", e);
        }
    }

    private String buildDetail(String description, String httpMethod, String requestUri, Throwable cause) {
        StackTraceElement[] frames = cause.getStackTrace();
        String stackTrace = frames.length == 0
                ? "(스택트레이스 없음)"
                : Arrays.stream(frames).limit(5).map(StackTraceElement::toString)
                        .collect(Collectors.joining("\n  at "));

        return String.format("""
                **메시지:** %s
                **API:** `[%s] %s`
                **쓰레드:** `%s`
                **발생 지점:**
                ```
                %s: %s
                  at %s
                ```""",
                description,
                httpMethod,
                requestUri,
                Thread.currentThread().getName(),
                cause.toString(),
                stackTrace);
    }
}
