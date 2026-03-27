package team.themoment.everygsm.server.v2.domain.project.repository.custom;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.entity.constant.Status;

public interface ProjectRepositoryCustom {
    List<ProjectJpaEntity> findLikedProjectsByUserId(Long userId);
    List<ProjectJpaEntity> findRegisteredProjectsByUserId(Long userId);
    List<ProjectJpaEntity> findByUserIdAndStatus(Long userId, Status status);
    List<ProjectJpaEntity> findAllByStatusWithCollections(Status status);
    void markUnseenProjectsAsInactive(Set<Long> seenIds);
    Optional<ProjectJpaEntity> findProjectWithCollectionsById(Long projectId);
    Optional<ProjectJpaEntity> findProjectWithCollectionsByIdAndUserId(Long projectId, Long userId);
    Optional<ProjectJpaEntity> findProjectWithCollectionsByIdAndStatus(Long projectId, Status status);
}
