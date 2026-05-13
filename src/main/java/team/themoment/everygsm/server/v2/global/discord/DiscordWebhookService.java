package team.themoment.everygsm.server.v2.global.discord;

import java.util.Arrays;
import java.util.Set;
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
            String threadName,
            Throwable cause) {
        sendServerError(title, description, httpMethod, requestUri, null, null, threadName, cause);
    }

    @Async
    public void sendServerError(String title,
            String description,
            String httpMethod,
            String requestUri,
            String clientIp,
            String host,
            String threadName,
            Throwable cause) {
        try {
            String detail = buildDetail(description, httpMethod, requestUri, clientIp, host, threadName, cause);
            discordWebhookClient.send(DiscordWebhookPayload.serverError(title, detail));
        } catch (Exception e) {
            log.warn("[DISCORD-WEBHOOK] 알림 전송 실패", e);
        }
    }

    @Async
    public void sendProjectRegistered(String projectTitle,
            String affiliation,
            String description,
            int startYear,
            String prodUrl,
            Set<String> stackNames,
            Set<String> repoUrls,
            String userName,
            String studentNumber,
            String userEmail) {
        try {
            String detail = buildProjectRegisteredDetail(projectTitle,
                    affiliation,
                    description,
                    startYear,
                    prodUrl,
                    stackNames,
                    repoUrls,
                    userName,
                    studentNumber,
                    userEmail);
            discordWebhookClient.send(DiscordWebhookPayload.projectRegistered("새로운 프로젝트 등록 요청", detail));
        } catch (Exception e) {
            log.warn("[DISCORD-WEBHOOK] 알림 전송 실패", e);
        }
    }

    private String buildProjectRegisteredDetail(String projectTitle,
            String affiliation,
            String description,
            int startYear,
            String prodUrl,
            Set<String> stackNames,
            Set<String> repoUrls,
            String userName,
            String studentNumber,
            String userEmail) {
        String stacks = (stackNames == null || stackNames.isEmpty()) ? "(없음)" : String.join(", ", stackNames);
        String repos = (repoUrls == null || repoUrls.isEmpty()) ? "(없음)" : String.join(", ", repoUrls);
        String prod = (prodUrl == null || prodUrl.isBlank()) ? "(없음)" : prodUrl;

        return String.format("""
                **프로젝트:** %s
                **소속:** %s
                **시작연도:** %d
                **설명:** %s
                **기술스택:** %s
                **배포 URL:** %s
                **저장소:** %s
                **신청자:** %s (%s, %s)""",
                projectTitle,
                affiliation,
                startYear,
                description,
                stacks,
                prod,
                repos,
                userName,
                studentNumber,
                userEmail);
    }

    private String buildDetail(String description,
            String httpMethod,
            String requestUri,
            String clientIp,
            String host,
            String threadName,
            Throwable cause) {
        StackTraceElement[] frames = cause.getStackTrace();
        String stackTrace = frames.length == 0
                ? "(스택트레이스 없음)"
                : Arrays.stream(frames).limit(5).map(StackTraceElement::toString)
                        .collect(Collectors.joining("\n  at "));

        String ipInfo = clientIp != null ? clientIp : "N/A";
        String hostInfo = host != null ? host : "N/A";

        return String.format("""
                **메시지:** %s
                **API:** `[%s] %s`
                **클라이언트 IP:** `%s`
                **Host:** `%s`
                **쓰레드:** `%s`
                **발생 지점:**
                ```
                %s: %s
                  at %s
                ```""",
                description,
                httpMethod,
                requestUri,
                ipInfo,
                hostInfo,
                threadName,
                cause.getClass().getName(),
                cause.getMessage(),
                stackTrace);
    }
}
