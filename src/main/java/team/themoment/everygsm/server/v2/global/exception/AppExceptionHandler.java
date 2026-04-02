package team.themoment.everygsm.server.v2.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import team.themoment.everygsm.server.v2.global.discord.DiscordWebhookClient;
import team.themoment.everygsm.server.v2.global.discord.DiscordWebhookPayload;
import team.themoment.everygsm.server.v2.global.exception.error.ExpectedException;
import team.themoment.sdk.response.CommonApiResponse;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class AppExceptionHandler {

    private final DiscordWebhookClient discordWebhookClient;

    @ExceptionHandler(ExpectedException.class)
    public ResponseEntity handleExpectedException(ExpectedException e) {
        if (e.getStatusCode().is5xxServerError()) {
            sendDiscordAlert("서버 오류 발생", e.getMessage());
        }
        return ResponseEntity.status(e.getStatusCode())
                .body(CommonApiResponse.error(e.getMessage(), e.getStatusCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleUnexpectedException(Exception e) {
        log.error("[UNHANDLED-EXCEPTION]", e);
        sendDiscordAlert("예상치 못한 서버 오류 발생", e.getClass().getSimpleName() + ": " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CommonApiResponse
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private void sendDiscordAlert(String title, String description) {
        try {
            discordWebhookClient.send(DiscordWebhookPayload.serverError(title, description));
        } catch (Exception ex) {
            log.warn("[DISCORD-WEBHOOK] 알림 전송 실패: {}", ex.getMessage());
        }
    }
}
