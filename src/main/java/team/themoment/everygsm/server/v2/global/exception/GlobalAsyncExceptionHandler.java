package team.themoment.everygsm.server.v2.global.exception;

import java.lang.reflect.Method;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import team.themoment.everygsm.server.v2.global.discord.DiscordWebhookClient;
import team.themoment.everygsm.server.v2.global.discord.DiscordWebhookPayload;

@Slf4j
@RequiredArgsConstructor
@Component
public class GlobalAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    private final DiscordWebhookClient discordWebhookClient;

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        log.error("[ASYNC-DISCORD-ERROR] method: {}, exception: {}", method.getName(), ex);
        try {
            discordWebhookClient.send(DiscordWebhookPayload.serverError("비동기 서버 오류 발생",
                    "method: " + method.getName() + "\n" + ex.getClass().getSimpleName() + ": " + ex.getMessage()));
        } catch (Exception e) {
            log.warn("[DISCORD-WEBHOOK] 알림 전송 실패: {}", e.getMessage());
        }
    }
}
