package team.themoment.everygsm.server.v2.domain.project.service;

import static team.themoment.everygsm.server.v2.domain.project.entity.constant.Status.APPROVED;
import static team.themoment.everygsm.server.v2.domain.project.entity.constant.Status.PENDING;

import java.util.List;
import java.util.Set;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.everygsm.server.v2.domain.project.dto.common.RepositoryDto;
import team.themoment.everygsm.server.v2.domain.project.dto.common.TechStackDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.ProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.QueryProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectLikeRepository;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectRepository;
import team.themoment.everygsm.server.v2.domain.user.entity.constant.Role;

@Service
@RequiredArgsConstructor
public class QueryProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectLikeRepository projectLikeRepository;

    @Transactional
    public QueryProjectResDto execute(@Nullable Role role, @Nullable Long userId) {
        return buildQueryResDto(role, userId);
    }

    private QueryProjectResDto buildQueryResDto(@Nullable Role role, @Nullable Long userId) {
        if (role == null) {
            return buildGuestQuery();
        }
        return switch (role) {
            case USER -> buildUserQuery(userId);
            case ADMIN -> buildAdminQuery();
        };
    }

    private QueryProjectResDto buildGuestQuery() {
        List<ProjectJpaEntity> projects = projectRepository.findByStatus(APPROVED);
        List<ProjectResDto> res = projects.stream().map(this::toRes).toList();

        return new QueryProjectResDto(res);
    }

    private QueryProjectResDto buildUserQuery(Long userId) {
        List<ProjectJpaEntity> projects = projectRepository.findByStatus(APPROVED);

        List<Long> projectIds = projects.stream().map(ProjectJpaEntity::getId).toList();

        Set<Long> likedProjectIds = new java.util.HashSet<>(
                projectLikeRepository.findProjectId(userId, projectIds));

        List<ProjectResDto> res = projects.stream().map(p -> toUserRes(p, likedProjectIds.contains(p.getId())))
                .toList();

        return new QueryProjectResDto(res);
    }

    private QueryProjectResDto buildAdminQuery() {
        List<ProjectJpaEntity> projects = projectRepository.findByStatus(PENDING);
        List<ProjectResDto> res = projects.stream().map(this::toRes).toList();

        return new QueryProjectResDto(res);
    }

    private ProjectResDto toRes(ProjectJpaEntity project) {
        List<TechStackDto> techStacks = extractTechStacks(project);
        List<RepositoryDto> repositories = extractRepositories(project);

        return new ProjectResDto(project.getId(), project.getLogo(), project.getTitle(), project.getAffiliation(),
                project.getDescription(), project.getProdUrl(), project.getStatus(), project.getReason(),
                project.getCreatedAt(), techStacks, repositories, false);
    }

    private ProjectResDto toUserRes(ProjectJpaEntity project, boolean liked) {
        List<TechStackDto> techStacks = extractTechStacks(project);
        List<RepositoryDto> repositories = extractRepositories(project);

        return new ProjectResDto(project.getId(), project.getLogo(), project.getTitle(), project.getAffiliation(),
                project.getDescription(), project.getProdUrl(), project.getStatus(), project.getReason(),
                project.getCreatedAt(), techStacks, repositories, liked);
    }

    private List<TechStackDto> extractTechStacks(ProjectJpaEntity project) {
        return project.getStackNames().stream().map(TechStackDto::new).toList();
    }

    private List<RepositoryDto> extractRepositories(ProjectJpaEntity project) {
        return project.getRepoUrls().stream().map(RepositoryDto::new).toList();
    }
}
