package team.themoment.everygsm.server.v2.domain.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.entity.constant.Status;

public interface ProjectRepository extends JpaRepository<ProjectJpaEntity, Long>, ProjectRepositoryCustom {
    List<ProjectJpaEntity> findByStatus(Status status);

    Optional<ProjectJpaEntity> findByExternalProjectId(Long externalProjectId);
}
