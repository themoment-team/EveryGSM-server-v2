package team.themoment.everygsm.server.v2.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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
            discordWebhookService.sendServerError("서버 오류 발생",
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI(),
                    getClientIp(request),
                    request.getServerName(),
                    Thread.currentThread().getName(),
                    e);
        }
        return ResponseEntity.status(e.getStatusCode())
                .body(CommonApiResponse.error(e.getMessage(), e.getStatusCode()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity handleNoResourceFoundException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(CommonApiResponse.error(HttpStatus.NOT_FOUND.getReasonPhrase(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity handleHttpMessageNotReadableException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonApiResponse.error("요청 바디를 읽을 수 없습니다.", HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().stream().map(err -> err.getDefaultMessage()).findFirst()
                .orElse(HttpStatus.BAD_REQUEST.getReasonPhrase());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonApiResponse.error(message, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonApiResponse.error("필수 파라미터가 누락되었습니다: " + e.getParameterName(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonApiResponse.error("파라미터 타입이 올바르지 않습니다: " + e.getName(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleUnexpectedException(Exception e, HttpServletRequest request) {
        log.error("[UNHANDLED-EXCEPTION]", e);
        discordWebhookService.sendServerError("예상치 못한 서버 오류 발생",
                e.getMessage(),
                request.getMethod(),
                request.getRequestURI(),
                getClientIp(request),
                request.getServerName(),
                Thread.currentThread().getName(),
                e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CommonApiResponse
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isBlank()) {
            return ip.trim();
        }
        return request.getRemoteAddr();
    }
}
