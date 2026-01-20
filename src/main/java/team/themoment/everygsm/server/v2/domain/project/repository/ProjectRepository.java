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
        select r
        from ProjectJpaEntity p
        join p.repoUrls r
        where p.id = :projectId
    """)
    Set<String> findRepoUrlsByProjectId(@Param("projectId") Long projectId);

    @Query("""
        select s
        from ProjectJpaEntity p
        join p.stackNames s
        where p.id = :projectId
    """)
    Set<String> findStackNamesByProjectId(@Param("projectId") Long projectId);
}
