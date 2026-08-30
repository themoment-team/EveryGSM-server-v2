package team.themoment.everygsm.server.v2.global.ratelimit;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import team.themoment.sdk.response.CommonApiResponse;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private static final String DATAGSM_EVENT_PATH = "/api/v2/projects/datagsm-events";

    private final RateLimitProperties properties;
    private final ObjectMapper objectMapper;

    private final ConcurrentHashMap<String, RequestCounter> counterMap = new ConcurrentHashMap<>();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return DATAGSM_EVENT_PATH.equals(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String ip = resolveClientIp(request);
        long windowSeconds = properties.windowSeconds();
        long currentWindow = System.currentTimeMillis() / (windowSeconds * 1000);

        int[] countHolder = new int[1];
        counterMap.compute(ip, (_, existing) -> {
            RequestCounter counter = (existing == null || existing.window() != currentWindow)
                    ? new RequestCounter(currentWindow)
                    : existing;
            countHolder[0] = counter.count().incrementAndGet();
            return counter;
        });

        if (countHolder[0] > properties.maxRequests()) {
            log.warn("Rate limit 초과 - IP: {}, 요청 수: {}", ip, countHolder[0]);
            writeRateLimitResponse(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank()) {
            return xRealIp.trim();
        }
        return request.getRemoteAddr();
    }

    private void writeRateLimitResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        CommonApiResponse<?> errorResponse = CommonApiResponse.error("요청이 너무 많습니다. 잠시 후 다시 시도해주세요.",
                HttpStatus.TOO_MANY_REQUESTS);

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    @Scheduled(fixedDelay = 60_000)
    public void evictStaleCounters() {
        long currentWindow = System.currentTimeMillis() / (properties.windowSeconds() * 1000);
        counterMap.entrySet().removeIf(entry -> entry.getValue().window() < currentWindow);
    }

    private record RequestCounter(long window, AtomicInteger count) {
        RequestCounter(long window) {
            this(window, new AtomicInteger(0));
        }
    }
}
