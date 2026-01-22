package team.themoment.everygsm.server.v2.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import team.themoment.everygsm.server.v2.domain.user.entity.UserJpaEntity;

public interface UserRepository extends JpaRepository<UserJpaEntity, Long> {
}
