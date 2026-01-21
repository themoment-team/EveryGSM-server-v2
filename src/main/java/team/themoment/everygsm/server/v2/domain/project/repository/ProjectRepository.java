package team.themoment.everygsm.server.v2.domain.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;

import java.util.Set;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectJpaEntity, Long> {

    @Query("""
        SELECT r
        FROM ProjectJpaEntity p
        JOIN p.repoUrls r
        WHERE p.id = :projectId
    """)
    Set<String> findRepoUrlsByProjectId(@Param("projectId") Long projectId);

    @Query("""
        SELECT s
        FROM ProjectJpaEntity p
        JOIN p.stackNames s
        WHERE p.id = :projectId
    """)
    Set<String> findStackNamesByProjectId(@Param("projectId") Long projectId);
}
