package team.themoment.everygsm.server.v2.domain.project.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectLikeRepository;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectRepository;
import team.themoment.everygsm.server.v2.global.exception.error.ExpectedException;

@Service
@RequiredArgsConstructor
public class DeleteProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectLikeRepository projectLikeRepository;

    @Transactional
    public void execute(Long userId, Long projectId) {
        ProjectJpaEntity project = projectRepository.findProjectWithCollectionsByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ExpectedException("프로젝트를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        // 이 프로젝트를 원본으로 참조하는 복사본(수정 대기 등)을 먼저 정리해야 자기참조 FK 위반을 피할 수 있다.
        for (ProjectJpaEntity copy : projectRepository.findByOriginalProjectId(projectId)) {
            projectLikeRepository.deleteByProjectId(copy.getId());
            projectRepository.delete(copy);
        }

        projectLikeRepository.deleteByProjectId(projectId);
        projectRepository.delete(project);
    }
}
