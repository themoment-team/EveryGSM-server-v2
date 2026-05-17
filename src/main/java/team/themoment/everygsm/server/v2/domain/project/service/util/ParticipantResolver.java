package team.themoment.everygsm.server.v2.domain.project.service.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.user.entity.UserJpaEntity;
import team.themoment.everygsm.server.v2.domain.user.repository.UserRepository;
import team.themoment.everygsm.server.v2.global.exception.error.ExpectedException;

@Component
@RequiredArgsConstructor
public class ParticipantResolver {

    private final UserRepository userRepository;

    public Set<UserJpaEntity> resolve(List<Long> requestedIds, UserJpaEntity owner) {
        Set<Long> distinctIds = new HashSet<>(requestedIds != null ? requestedIds : List.of());
        distinctIds.remove(owner.getId());

        List<UserJpaEntity> found = distinctIds.isEmpty() ? List.of() : userRepository.findAllById(distinctIds);
        if (found.size() != distinctIds.size()) {
            throw new ExpectedException("존재하지 않는 참여자가 포함되어 있습니다.", HttpStatus.BAD_REQUEST);
        }

        Set<UserJpaEntity> result = new HashSet<>(found);
        result.add(owner);
        return result;
    }
}
