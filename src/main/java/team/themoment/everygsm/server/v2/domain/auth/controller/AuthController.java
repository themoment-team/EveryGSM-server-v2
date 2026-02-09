package team.themoment.everygsm.server.v2.domain.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.auth.dto.request.OAuthSignInReqDto;
import team.themoment.everygsm.server.v2.domain.auth.dto.response.OAuthSignInResDto;
import team.themoment.everygsm.server.v2.domain.auth.service.OAuthSignInService;

@RestController
@RequestMapping("/api/v2/auth")
@RequiredArgsConstructor
public class AuthController {
    private final OAuthSignInService oAuthSignInService;

    @PostMapping("/signin")
    public OAuthSignInResDto signIn(@RequestBody @Valid OAuthSignInReqDto reqDto) {
        return oAuthSignInService.execute(reqDto);
    }
}
