package team.themoment.everygsm.server.v2.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import team.themoment.everygsm.server.v2.global.exception.error.ExpectedException;
import team.themoment.sdk.response.CommonApiResponse;

@RestControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(ExpectedException.class)
    public ResponseEntity handleExpectedException(ExpectedException e) {
        return ResponseEntity.status(e.getStatusCode())
                .body(CommonApiResponse.error(e.getMessage(), e.getStatusCode()));
    }
}
