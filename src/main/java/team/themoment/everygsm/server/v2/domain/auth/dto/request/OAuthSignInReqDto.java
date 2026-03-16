package team.themoment.everygsm.server.v2.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "OAuth 로그인 요청")
public record OAuthSignInReqDto(
        @Schema(description = "DataGSM OAuth 인증 코드", example = "abc123xyz", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank String authCode) {
}
