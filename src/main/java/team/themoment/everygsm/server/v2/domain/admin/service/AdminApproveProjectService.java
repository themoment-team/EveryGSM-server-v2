package team.themoment.everygsm.server.v2.domain.admin.service;

import static team.themoment.everygsm.server.v2.domain.project.entity.constant.Status.APPROVED;

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
public class AdminApproveProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Transactional
    public ProjectResDto execute(Long projectId) {
        return projectMapper.toRes(approveProject(projectId), false);
    }

    private ProjectJpaEntity approveProject(Long projectId) {
        ProjectJpaEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ExpectedException("해당 프로젝트가 존재하지 않습니다.", HttpStatus.NOT_FOUND));
        project.updateStatus(APPROVED, null);
        return project;
    }
}
