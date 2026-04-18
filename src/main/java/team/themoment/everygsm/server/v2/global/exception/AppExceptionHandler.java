package team.themoment.everygsm.server.v2.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import team.themoment.everygsm.server.v2.global.discord.DiscordWebhookService;
import team.themoment.everygsm.server.v2.global.exception.error.ExpectedException;
import team.themoment.sdk.response.CommonApiResponse;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class AppExceptionHandler {

    private final DiscordWebhookService discordWebhookService;

    @ExceptionHandler(ExpectedException.class)
    public ResponseEntity handleExpectedException(ExpectedException e, HttpServletRequest request) {
        // ExpectedException을 사용하는 의도적인 5xx 에러 반환에 대해서도 디스코드 메시지 전송
        if (e.getStatusCode().is5xxServerError()) {
            discordWebhookService
                    .sendServerError("서버 오류 발생", e.getMessage(), request.getMethod(), request.getRequestURI(), e);
        }
        return ResponseEntity.status(e.getStatusCode())
                .body(CommonApiResponse.error(e.getMessage(), e.getStatusCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleUnexpectedException(Exception e, HttpServletRequest request) {
        log.error("[UNHANDLED-EXCEPTION]", e);
        discordWebhookService
                .sendServerError("예상치 못한 서버 오류 발생", e.getMessage(), request.getMethod(), request.getRequestURI(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CommonApiResponse
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
