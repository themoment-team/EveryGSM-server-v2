package team.themoment.everygsm.server.v2.domain.project.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.project.dto.common.RepositoryDto;
import team.themoment.everygsm.server.v2.domain.project.dto.common.TechStackDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.ProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectLikeRepository;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectRepository;
import team.themoment.everygsm.server.v2.global.exception.error.ExpectedException;

@Service
@RequiredArgsConstructor
public class QueryMyProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectLikeRepository projectLikeRepository;

    @Transactional(readOnly = true)
    public ProjectResDto execute(Long userId, Long projectId) {
        ProjectJpaEntity project = projectRepository.findProjectWithCollectionsById(projectId)
                .orElseThrow(() -> new ExpectedException("해당 프로젝트가 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        if (!project.getUser().getId().equals(userId)) {
            throw new ExpectedException("자신의 프로젝트가 아닙니다.", HttpStatus.FORBIDDEN);
        }

        boolean liked = projectLikeRepository.findByUserIdAndProjectId(userId, projectId).isPresent();

        return new ProjectResDto(
                project.getId(),
                project.getLogo(),
                project.getTitle(),
                project.getAffiliation(),
                project.getDescription(),
                project.getProdUrl(),
                project.getStatus(),
                project.getReason(),
                project.getCreatedAt(),
                project.getStackNames().stream().map(TechStackDto::new).toList(),
                project.getRepoUrls().stream().map(RepositoryDto::new).toList(),
                liked
        );
    }
}
