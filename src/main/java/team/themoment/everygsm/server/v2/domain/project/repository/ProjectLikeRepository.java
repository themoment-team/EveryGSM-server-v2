package team.themoment.everygsm.server.v2.domain.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.themoment.everygsm.server.v2.domain.project.entity.LikeJpaEntity;

import java.util.List;

public interface ProjectLikeRepository extends JpaRepository<LikeJpaEntity, Long> {
    @Query("""
        SELECT pl.project.id
        FROM LikeJpaEntity pl
        WHERE pl.user.id = :userId
          AND pl.project.id in :projectIds
    """)
    List<Long> findLikedProjectIds(@Param("userId") Long userId,
                                   @Param("projectIds") List<Long> projectIds);
}
