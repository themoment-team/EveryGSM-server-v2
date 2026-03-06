package team.themoment.everygsm.server.v2.domain.project.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.project.dto.common.RepositoryDto;
import team.themoment.everygsm.server.v2.domain.project.dto.common.TechStackDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.ProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectLikeRepository;
import team.themoment.everygsm.server.v2.global.exception.error.ExpectedException;

@Service
@RequiredArgsConstructor
public class DeleteProjectLikeService {

    private final ProjectLikeRepository projectLikeRepository;

    @Transactional
    public ProjectResDto execute(Long userId, Long projectId) {
        ProjectJpaEntity project = projectLikeRepository
                .findProjectWithCollectionsByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new ExpectedException("좋아요한 프로젝트가 아닙니다.", HttpStatus.NOT_FOUND));

        projectLikeRepository.deleteByUserIdAndProjectId(userId, projectId);

        return buildProjectResDto(project);
    }

    private ProjectResDto buildProjectResDto(ProjectJpaEntity project) {
        List<TechStackDto> techStacks = project.getStackNames().stream().map(TechStackDto::new).toList();

        List<RepositoryDto> repositories = project.getRepoUrls().stream()
                .map(r -> new RepositoryDto(r.getRepoName(), r.getRepoUrl())).toList();

        return new ProjectResDto(project.getId(),
                project.getLogo(),
                project.getTitle(),
                project.getAffiliation(),
                project.getDescription(),
                project.getProdUrl(),
                project.getStatus(),
                project.getReason(),
                project.getCreatedAt(),
                techStacks,
                repositories,
                false);
    }
}
