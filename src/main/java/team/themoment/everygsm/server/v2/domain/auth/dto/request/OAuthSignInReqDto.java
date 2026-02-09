package team.themoment.everygsm.server.v2.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record OAuthSignInReqDto(@NotBlank String authCode) {
}
