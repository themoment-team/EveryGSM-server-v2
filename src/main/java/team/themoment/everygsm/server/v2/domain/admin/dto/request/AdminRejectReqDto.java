package team.themoment.everygsm.server.v2.domain.admin.dto.request;

import software.amazon.awssdk.annotations.NotNull;

public record AdminRejectReqDto(@NotNull String reason) {
}
