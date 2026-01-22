package team.themoment.everygsm.server.v2.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import team.themoment.everygsm.server.v2.domain.user.entity.UserJpaEntity;

@Repository
public interface UserRepository extends JpaRepository<UserJpaEntity, Long> {
}
