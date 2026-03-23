package team.themoment.everygsm.server.v2.domain.auth.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import team.themoment.datagsm.sdk.oauth.DataGsmOAuthClient;
import team.themoment.datagsm.sdk.oauth.exception.DataGsmException;
import team.themoment.datagsm.sdk.oauth.model.Student;
import team.themoment.datagsm.sdk.oauth.model.TokenResponse;
import team.themoment.datagsm.sdk.oauth.model.UserInfo;
import team.themoment.everygsm.server.v2.domain.auth.dto.request.OAuthSignInReqDto;
import team.themoment.everygsm.server.v2.domain.auth.dto.response.OAuthSignInResDto;
import team.themoment.everygsm.server.v2.domain.user.entity.UserJpaEntity;
import team.themoment.everygsm.server.v2.domain.user.entity.constant.Role;
import team.themoment.everygsm.server.v2.domain.user.repository.UserRepository;
import team.themoment.everygsm.server.v2.domain.user.util.UserCreator;
import team.themoment.everygsm.server.v2.global.exception.error.ExpectedException;
import team.themoment.everygsm.server.v2.global.security.jwt.JwtTokenProvider;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthSignInService {

    private final DataGsmOAuthClient dataGsmClient;
    private final UserRepository userRepository;
    private final UserCreator userCreator;
    private final JwtTokenProvider jwtTokenProvider;
    @Value("${oauth.datagsm.redirect-uris}")
    private Set<String> allowedRedirectUris;

    private static final int LOCK_STRIPE_COUNT = 64;
    private final Object[] lockStripes = createStripes(LOCK_STRIPE_COUNT);

    private static Object[] createStripes(int count) {
        Object[] arr = new Object[count];
        for (int i = 0; i < count; i++)
            arr[i] = new Object();
        return arr;
    }

    private Object getLock(String email) {
        return lockStripes[Math.abs(email.hashCode() % LOCK_STRIPE_COUNT)];
    }

    @Transactional
    public OAuthSignInResDto execute(OAuthSignInReqDto reqDto) {
        String redirectUri = reqDto.redirectUri();
        if (!allowedRedirectUris.contains(redirectUri)) {
            throw new ExpectedException("허용되지 않은 redirectUri입니다.", HttpStatus.BAD_REQUEST);
        }

        try {
            TokenResponse tokenResponse = dataGsmClient.exchangeCodeForToken(reqDto.authCode(), redirectUri);
            log.info("엑세스 토큰 : %s, 리프레시 토큰 : %s".formatted(tokenResponse.getAccessToken(),
                    tokenResponse.getRefreshToken()));
            UserInfo userInfo = dataGsmClient.getUserInfo(tokenResponse.getAccessToken());

            if (!userInfo.isStudent()) {
                throw new ExpectedException("학생만 로그인할 수 있습니다.", HttpStatus.FORBIDDEN);
            }

            Student student = userInfo.getStudent();
            if (student == null || student.getName() == null || student.getStudentNumber() == null) {
                throw new ExpectedException("학생 정보가 유효하지 않습니다.", HttpStatus.BAD_REQUEST);
            }

            UserJpaEntity user = getOrCreateUser(userInfo.getEmail(), student.getName(), student.getStudentNumber());

            String jwt = jwtTokenProvider.createToken(user.getId(), user.getRole().name());

            return new OAuthSignInResDto(jwt);

        } catch (DataGsmException e) {
            log.error("DataGSM OAuth error: status={}, message={}", e.getStatusCode(), e.getMessage());
            throw new ExpectedException("OAuth 인증에 실패했습니다.", HttpStatus.valueOf(e.getStatusCode()));
        }
    }

    private UserJpaEntity getOrCreateUser(String email, String name, Integer studentNumber) {
        synchronized (getLock(email)) {
            return userRepository.findByEmail(email).orElseGet(() -> {
                UserJpaEntity newUser = UserJpaEntity.builder().email(email).name(name)
                        .studentNumber(String.valueOf(studentNumber)).role(Role.USER).build();
                return userCreator.execute(newUser);
            });
        }
    }
}
