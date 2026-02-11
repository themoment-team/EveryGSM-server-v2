package team.themoment.everygsm.server.v2.domain.auth.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import team.themoment.datagsm.sdk.oauth.DataGsmClient;
import team.themoment.datagsm.sdk.oauth.exception.DataGsmException;
import team.themoment.datagsm.sdk.oauth.model.Student;
import team.themoment.datagsm.sdk.oauth.model.TokenResponse;
import team.themoment.datagsm.sdk.oauth.model.UserInfo;
import team.themoment.everygsm.server.v2.domain.auth.dto.request.OAuthSignInReqDto;
import team.themoment.everygsm.server.v2.domain.auth.dto.response.OAuthSignInResDto;
import team.themoment.everygsm.server.v2.domain.user.entity.UserJpaEntity;
import team.themoment.everygsm.server.v2.domain.user.repository.UserRepository;
import team.themoment.everygsm.server.v2.domain.user.service.CreateUserService;
import team.themoment.everygsm.server.v2.global.exception.error.ExpectedException;
import team.themoment.everygsm.server.v2.global.security.jwt.JwtTokenProvider;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthSignInService {

    private final DataGsmClient dataGsmClient;
    private final UserRepository userRepository;
    private final CreateUserService createUserService;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public OAuthSignInResDto execute(OAuthSignInReqDto reqDto) {
        try {
            TokenResponse tokenResponse = dataGsmClient.exchangeToken(reqDto.authCode());
            UserInfo userInfo = dataGsmClient.getUserInfo(tokenResponse.getAccessToken());

            if (!userInfo.isStudent()) {
                throw new ExpectedException("학생만 로그인할 수 있습니다.", HttpStatus.FORBIDDEN);
            }

            Student student = userInfo.getStudent();
            if (student == null || student.getName() == null || student.getStudentNumber() == null) {
                throw new ExpectedException("학생 정보가 유효하지 않습니다.", HttpStatus.BAD_REQUEST);
            }

            UserJpaEntity user = userRepository.findByEmail(userInfo.getEmail()).orElseGet(() -> createUserService
                    .execute(userInfo.getEmail(), student.getName(), student.getStudentNumber()));

            String jwt = jwtTokenProvider.createToken(user.getId(), user.getRole().name());

            return new OAuthSignInResDto(jwt);

        } catch (DataGsmException e) {
            log.error("DataGSM OAuth error: status={}, message={}", e.getStatusCode(), e.getMessage());
            throw new ExpectedException("OAuth 인증에 실패했습니다: " + e.getMessage(), HttpStatus.valueOf(e.getStatusCode()));
        }
    }
}
