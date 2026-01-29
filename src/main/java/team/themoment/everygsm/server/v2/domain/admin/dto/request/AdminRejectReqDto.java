package team.themoment.everygsm.server.v2.domain.admin.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AdminRejectReqDto(@NotBlank String reason) {
}
