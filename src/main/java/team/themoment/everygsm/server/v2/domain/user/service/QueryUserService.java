package team.themoment.everygsm.server.v2.domain.user.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.user.dto.response.UserResDto;
import team.themoment.everygsm.server.v2.domain.user.entity.UserJpaEntity;
import team.themoment.everygsm.server.v2.domain.user.repository.UserRepository;
import team.themoment.everygsm.server.v2.global.exception.error.ExpectedException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryUserService {

    private final UserRepository userRepository;

    public UserResDto execute(Long userId) {
        UserJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExpectedException(HttpStatus.NOT_FOUND));

        return new UserResDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getStudentNumber(),
                user.getRole()
        );
    }
}
