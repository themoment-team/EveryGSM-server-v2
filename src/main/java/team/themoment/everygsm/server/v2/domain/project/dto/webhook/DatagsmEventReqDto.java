package team.themoment.everygsm.server.v2.domain.project.dto.webhook;

public record DatagsmEventReqDto(String id, String event, String timestamp, DatagsmEventDataDto data) {
}
