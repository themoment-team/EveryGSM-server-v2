package team.themoment.everygsm.server.v2.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "OAuth 로그인 요청")
public record OAuthSignInReqDto(
        @Schema(description = "DataGSM OAuth 인증 코드", example = "abc123xyz", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank String authCode,
        @Schema(description = "OAuth 리다이렉트 URI", example = "http://localhost:3000", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank String redirectUri,
        @Schema(description = "OAuth Verifier 코드", example = "abcd123xyz", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank String codeVerifier) {
}
