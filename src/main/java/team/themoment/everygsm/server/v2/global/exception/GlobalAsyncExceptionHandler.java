package team.themoment.everygsm.server.v2.global.exception;

import java.lang.reflect.Method;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import team.themoment.everygsm.server.v2.global.discord.DiscordWebhookService;

@Slf4j
@RequiredArgsConstructor
@Component
public class GlobalAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    private final DiscordWebhookService discordWebhookService;

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        log.error("[ASYNC-DISCORD-ERROR] method: {}, exception: {}", method.getName(), ex);
        discordWebhookService.sendServerError("비동기 서버 오류 발생",
                ex.getClass().getSimpleName() + ": " + ex.getMessage(),
                "ASYNC",
                method.getDeclaringClass().getSimpleName() + "." + method.getName(),
                ex);
    }
}
