package team.themoment.everygsm.server.v2.domain.admin.service;

import static team.themoment.everygsm.server.v2.domain.project.entity.constant.Status.PENDING;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.project.dto.response.ProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.mapper.ProjectMapper;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectRepository;
import team.themoment.everygsm.server.v2.global.exception.error.ExpectedException;

@Service
@RequiredArgsConstructor
public class AdminQueryPendingProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Transactional(readOnly = true)
    public ProjectResDto execute(Long projectId) {
        ProjectJpaEntity project = projectRepository.findProjectWithCollectionsById(projectId)
                .filter(p -> p.getStatus() == PENDING)
                .orElseThrow(() -> new ExpectedException("해당 승인 대기 프로젝트가 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        return projectMapper.toRes(project, false);
    }
}
