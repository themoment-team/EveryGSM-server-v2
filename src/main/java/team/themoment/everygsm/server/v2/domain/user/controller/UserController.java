package team.themoment.everygsm.server.v2.domain.user.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.user.dto.response.UserResDto;
import team.themoment.everygsm.server.v2.domain.user.service.QueryUserService;

@Tag(name = "User", description = "사용자 API")
@RestController
@RequestMapping("/api/v2/users")
@RequiredArgsConstructor
public class UserController {

    private final QueryUserService queryUserService;

    @Operation(summary = "내 정보 조회", description = "현재 로그인된 사용자의 정보를 조회합니다")
    @GetMapping("/me")
    public UserResDto queryMe(@AuthenticationPrincipal Long userId) {
        return queryUserService.execute(userId);
    }
}
