package team.themoment.everygsm.server.v2.domain.project.service;

import static team.themoment.everygsm.server.v2.domain.project.entity.constant.Status.APPROVED;
import static team.themoment.everygsm.server.v2.domain.project.entity.constant.Status.PENDING;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.project.dto.common.RepositoryDto;
import team.themoment.everygsm.server.v2.domain.project.dto.common.TechStackDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.ProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.ViewProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectLikeRepository;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectRepository;
import team.themoment.everygsm.server.v2.domain.user.entity.UserJpaEntity;
import team.themoment.everygsm.server.v2.domain.user.entity.constant.Role;
import team.themoment.everygsm.server.v2.domain.user.repository.UserRepository;
import team.themoment.everygsm.server.v2.global.exception.error.ExpectedException;

@Service
@RequiredArgsConstructor
public class ViewProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectLikeRepository projectLikeRepository;
    private final UserRepository userRepository;

    public ViewProjectResDto execute(@Nullable Role role, @Nullable Long userId) {
        return buildViewResDto(role, userId);
    }

    private ViewProjectResDto buildViewResDto(@Nullable Role role, @Nullable Long userId) {
        if (role == null) {
            return buildGuestView();
        }
        return switch (role) {
            case USER -> buildUserView(userId);
            case ADMIN -> buildAdminView();
        };
    }

    private ViewProjectResDto buildGuestView() {
        List<ProjectJpaEntity> projects = projectRepository.findByStatus(APPROVED);
        List<ProjectResDto> res = projects.stream().map(this::toRes).toList();

        return new ViewProjectResDto(res);
    }

    private ViewProjectResDto buildUserView(Long userId) {
        List<ProjectJpaEntity> projects = projectRepository.findByStatus(APPROVED);

        List<Long> projectIds = projects.stream()
                .map(ProjectJpaEntity::getId)
                .toList();

        Set<Long> likedProjectIds = new java.util.HashSet<>(
                projectLikeRepository.findLikedProjectIds(userId, projectIds)
        );

        List<ProjectResDto> res = projects.stream()
                .map(p -> toUserRes(p, likedProjectIds.contains(p.getId())))
                .toList();

        return new ViewProjectResDto(res);
    }

    private ViewProjectResDto buildAdminView() {
        List<ProjectJpaEntity> projects = projectRepository.findByStatus(PENDING);
        List<ProjectResDto> res = projects.stream().map(this::toRes).toList();

        return new ViewProjectResDto(res);
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
        return project.getStackNames().stream()
                .map(TechStackDto::new)
                .toList();
    }

    private List<RepositoryDto> extractRepositories(ProjectJpaEntity project) {
        return project.getRepoUrls().stream()
                .map(RepositoryDto::new)
                .toList();
    }
}
