package team.themoment.everygsm.server.v2.domain.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import team.themoment.everygsm.server.v2.domain.project.entity.LikeJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;
import team.themoment.everygsm.server.v2.domain.user.entity.UserJpaEntity;

public interface ProjectLikeRepository extends JpaRepository<LikeJpaEntity, Long> {
    boolean findByProjectAndUser(ProjectJpaEntity project, UserJpaEntity user);
}
