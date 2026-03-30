package team.themoment.everygsm.server.v2.domain.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import team.themoment.everygsm.server.v2.domain.project.entity.LikeJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.repository.custom.ProjectLikeRepositoryCustom;

public interface ProjectLikeRepository extends JpaRepository<LikeJpaEntity, Long>, ProjectLikeRepositoryCustom {
    @Query("""
                SELECT pl.project.id
                FROM LikeJpaEntity pl
                WHERE pl.user.id = :userId
                  AND pl.project.id in :projectIds
            """)
    List<Long> findByProjectId(@Param("userId") Long userId, @Param("projectIds") List<Long> projectIds);

    void deleteByUserIdAndProjectId(Long userId, Long projectId);

    Optional<LikeJpaEntity> findByUserIdAndProjectId(Long userId, Long projectId);

    boolean existsByProjectIdAndUserId(Long projectId, Long userId);
}
