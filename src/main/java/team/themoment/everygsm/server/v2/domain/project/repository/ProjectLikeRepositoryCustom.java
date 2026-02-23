package team.themoment.everygsm.server.v2.domain.project.repository;

import java.util.Optional;

import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;

public interface ProjectLikeRepositoryCustom {
    Optional<ProjectJpaEntity> findProjectWithCollectionsByUserIdAndProjectId(Long userId, Long projectId);
}
