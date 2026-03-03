package team.themoment.everygsm.server.v2.domain.project.repository.custom.impl;

import static team.themoment.everygsm.server.v2.domain.project.entity.QLikeJpaEntity.likeJpaEntity;
import static team.themoment.everygsm.server.v2.domain.project.entity.QProjectJpaEntity.projectJpaEntity;

import java.util.List;
import java.util.Set;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.entity.constant.Status;
import team.themoment.everygsm.server.v2.domain.project.repository.custom.ProjectRepositoryCustom;

@RequiredArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<ProjectJpaEntity> findLikedProjectsByUserId(Long userId) {
        return queryFactory.selectFrom(projectJpaEntity).leftJoin(projectJpaEntity.stackNames).fetchJoin()
                .leftJoin(projectJpaEntity.repoUrls).fetchJoin().join(likeJpaEntity)
                .on(likeJpaEntity.project.eq(projectJpaEntity)).where(likeJpaEntity.user.id.eq(userId))
                .orderBy(projectJpaEntity.createdAt.desc()).distinct().fetch();
    }

    @Override
    public List<ProjectJpaEntity> findRegisteredProjectsByUserId(Long userId) {
        return queryFactory.selectFrom(projectJpaEntity).leftJoin(projectJpaEntity.stackNames).fetchJoin()
                .leftJoin(projectJpaEntity.repoUrls).fetchJoin().where(projectJpaEntity.user.id.eq(userId))
                .orderBy(projectJpaEntity.createdAt.desc()).distinct().fetch();
    }

    @Override
    public List<ProjectJpaEntity> findByUserIdAndStatus(Long userId, Status status) {
        return queryFactory.selectFrom(projectJpaEntity).leftJoin(projectJpaEntity.stackNames).fetchJoin()
                .leftJoin(projectJpaEntity.repoUrls).fetchJoin()
                .where(projectJpaEntity.user.id.eq(userId).and(projectJpaEntity.status.eq(status)))
                .orderBy(projectJpaEntity.createdAt.desc()).distinct().fetch();
    }

    @Override
    public void markUnseenProjectsAsInactive(Set<Long> seenIds) {
        if (seenIds.isEmpty()) {
            return;
        }
        queryFactory.update(projectJpaEntity)
                .set(projectJpaEntity.status, Status.INACTIVE)
                .where(projectJpaEntity.externalProjectId.isNotNull()
                        .and(projectJpaEntity.externalProjectId.notIn(seenIds)))
                .execute();
    }
}
