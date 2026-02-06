package team.themoment.everygsm.server.v2.global.security.handler;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import team.themoment.everygsm.server.v2.global.common.response.CommonApiResponse;
import team.themoment.everygsm.server.v2.global.exception.error.ExpectedException;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        Exception exception = (Exception) request.getAttribute("exception");

        if (exception instanceof ExpectedException) {
            handleExpectedException(request, response, (ExpectedException) exception);
        } else {
            handleDefaultException(request, response, authException);
        }
    }

    private void handleExpectedException(HttpServletRequest request,
            HttpServletResponse response,
            ExpectedException exception) throws IOException {
        log.error("인증 실패: {}", exception.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(exception.getStatusCode().value());

        CommonApiResponse<?> errorResponse = CommonApiResponse.error(exception.getMessage(), exception.getStatusCode());

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    private void handleDefaultException(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        log.error("인증 실패: {}", authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        CommonApiResponse<?> errorResponse = CommonApiResponse.error("인증이 필요합니다. 유효한 JWT 토큰을 제공해주세요.",
                HttpStatus.UNAUTHORIZED);

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
