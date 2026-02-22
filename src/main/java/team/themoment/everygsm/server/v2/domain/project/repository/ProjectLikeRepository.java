package team.themoment.everygsm.server.v2.domain.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import team.themoment.everygsm.server.v2.domain.project.entity.LikeJpaEntity;

public interface ProjectLikeRepository extends JpaRepository<LikeJpaEntity, Long> {
    @Query("""
                SELECT pl.project.id
                FROM LikeJpaEntity pl
                WHERE pl.user.id = :userId
                  AND pl.project.id in :projectIds
            """)
    List<Long> findByProjectId(@Param("userId") Long userId, @Param("projectIds") List<Long> projectIds);

    @Query("""
                SELECT pl
                FROM LikeJpaEntity pl
                WHERE pl.user.id = :userId
                  AND pl.project.id = :projectId
            """)
    Optional<LikeJpaEntity> findByUserIdAndProjectId(@Param("userId") Long userId, @Param("projectId") Long projectId);
}
