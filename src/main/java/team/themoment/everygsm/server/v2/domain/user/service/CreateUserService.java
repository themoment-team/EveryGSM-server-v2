package team.themoment.everygsm.server.v2.domain.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.user.entity.UserJpaEntity;
import team.themoment.everygsm.server.v2.domain.user.entity.constant.Role;
import team.themoment.everygsm.server.v2.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CreateUserService {

    private final UserRepository userRepository;

    @Transactional
    public UserJpaEntity execute(String email, String name, Integer studentNumber) {
        UserJpaEntity newUser = UserJpaEntity.builder().email(email).name(name)
                .studentNumber(String.valueOf(studentNumber)).role(Role.USER).build();
        return userRepository.save(newUser);
    }
}
