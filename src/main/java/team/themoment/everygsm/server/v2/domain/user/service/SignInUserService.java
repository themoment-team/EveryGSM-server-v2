package team.themoment.everygsm.server.v2.domain.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.user.entity.UserJpaEntity;
import team.themoment.everygsm.server.v2.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class SignInUserService {

    private final UserRepository userRepository;
    private final CreateUserService createUserService;

    @Transactional
    public UserJpaEntity execute(String email, String name, Integer studentNumber) {
        return userRepository.findByEmail(email).orElseGet(() -> createUserService.execute(email, name, studentNumber));
    }
}
