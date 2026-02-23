package team.themoment.everygsm.server.v2.domain.project.repository.impl;

import static team.themoment.everygsm.server.v2.domain.project.entity.QLikeJpaEntity.likeJpaEntity;
import static team.themoment.everygsm.server.v2.domain.project.entity.QProjectJpaEntity.projectJpaEntity;

import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectLikeRepositoryCustom;

@RequiredArgsConstructor
public class ProjectLikeRepositoryImpl implements ProjectLikeRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<ProjectJpaEntity> findProjectWithCollectionsByUserIdAndProjectId(Long userId, Long projectId) {
        ProjectJpaEntity result = queryFactory.selectFrom(projectJpaEntity).leftJoin(projectJpaEntity.stackNames)
                .fetchJoin().leftJoin(projectJpaEntity.repoUrls).fetchJoin().join(likeJpaEntity)
                .on(likeJpaEntity.project.eq(projectJpaEntity).and(likeJpaEntity.user.id.eq(userId))
                        .and(likeJpaEntity.project.id.eq(projectId)))
                .distinct().fetchOne();

        return Optional.ofNullable(result);
    }
}
