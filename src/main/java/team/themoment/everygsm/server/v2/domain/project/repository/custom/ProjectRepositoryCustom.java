package team.themoment.everygsm.server.v2.domain.project.repository.custom;

import java.util.List;
import java.util.Optional;

import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.entity.constant.Status;

public interface ProjectRepositoryCustom {
    List<ProjectJpaEntity> findLikedProjectsByUserId(Long userId);
    List<ProjectJpaEntity> findRegisteredProjectsByUserId(Long userId);
    List<ProjectJpaEntity> findByUserIdAndStatus(Long userId, Status status);
    List<ProjectJpaEntity> findAllByStatusWithCollections(Status status);
    Optional<ProjectJpaEntity> findByIdWithCollections(Long projectId);
}
