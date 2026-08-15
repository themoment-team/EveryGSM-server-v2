package team.themoment.everygsm.server.v2.domain.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.entity.constant.Status;
import team.themoment.everygsm.server.v2.domain.project.repository.custom.ProjectRepositoryCustom;

public interface ProjectRepository extends JpaRepository<ProjectJpaEntity, Long>, ProjectRepositoryCustom {
    List<ProjectJpaEntity> findByStatus(Status status);

    Optional<ProjectJpaEntity> findByExternalProjectId(Long externalProjectId);

    Optional<ProjectJpaEntity> findByOriginalProjectIdAndStatus(Long originalProjectId, Status status);

    List<ProjectJpaEntity> findByOriginalProjectId(Long originalProjectId);

    /**
     * datagsm 등록 직후 external_project_id 저장이 유실된 프로젝트를 동기화 시 복구하기 위한 조회. 이미
     * external_project_id를 가진 행은 다른 datagsm 프로젝트에 매핑돼 있으므로 제외한다.
     */
    List<ProjectJpaEntity> findByExternalProjectIdIsNullAndTitleAndStartYearAndStatusNot(String title,
            Integer startYear,
            Status status);
}
