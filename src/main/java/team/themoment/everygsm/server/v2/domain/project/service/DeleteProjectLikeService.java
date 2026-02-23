package team.themoment.everygsm.server.v2.domain.project.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.project.dto.common.RepositoryDto;
import team.themoment.everygsm.server.v2.domain.project.dto.common.TechStackDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.ProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.entity.LikeJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectLikeRepository;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectRepository;
import team.themoment.everygsm.server.v2.global.exception.error.ExpectedException;

@Service
@RequiredArgsConstructor
public class DeleteProjectLikeService {

    private final ProjectLikeRepository projectLikeRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public ProjectResDto execute(Long userId, Long projectId) {
        LikeJpaEntity like = projectLikeRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new ExpectedException("좋아요한 프로젝트가 아닙니다.", HttpStatus.NOT_FOUND));

        projectLikeRepository.delete(like);

        ProjectJpaEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ExpectedException("해당 프로젝트가 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        return buildProjectResDto(project);
    }

    private ProjectResDto buildProjectResDto(ProjectJpaEntity project) {
        List<TechStackDto> techStacks = project.getStackNames().stream().map(TechStackDto::new).toList();

        List<RepositoryDto> repositories = project.getRepoUrls().stream().map(RepositoryDto::new).toList();

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
