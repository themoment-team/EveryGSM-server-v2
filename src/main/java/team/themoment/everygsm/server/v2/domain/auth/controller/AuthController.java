package team.themoment.everygsm.server.v2.domain.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.auth.dto.request.OAuthSignInReqDto;
import team.themoment.everygsm.server.v2.domain.auth.dto.response.OAuthSignInResDto;
import team.themoment.everygsm.server.v2.domain.auth.service.OAuthSignInService;

@Tag(name = "인증", description = "인증 API")
@RestController
@RequestMapping("/api/v2/auth")
@RequiredArgsConstructor
public class AuthController {
    private final OAuthSignInService oAuthSignInService;

    @Operation(summary = "OAuth 로그인", description = "DataGSM OAuth를 통해 로그인하고 JWT 토큰을 발급받습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = OAuthSignInResDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 authCode 또는 학생 정보)", content = @Content),
            @ApiResponse(responseCode = "401", description = "OAuth 인증 실패", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음 (학생만 로그인 가능)", content = @Content)})
    @PostMapping("/signin")
    public OAuthSignInResDto signIn(@RequestBody @Valid OAuthSignInReqDto reqDto) {
        return oAuthSignInService.execute(reqDto);
    }
}
