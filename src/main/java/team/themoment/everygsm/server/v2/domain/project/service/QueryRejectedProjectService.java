package team.themoment.everygsm.server.v2.domain.project.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.project.dto.response.ProjectListResDto;
import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.entity.constant.Status;
import team.themoment.everygsm.server.v2.domain.project.mapper.ProjectMapper;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectRepository;

@Service
@RequiredArgsConstructor
public class QueryRejectedProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Transactional(readOnly = true)
    public ProjectListResDto execute(Long userId) {
        List<ProjectJpaEntity> projects = projectRepository.findByUserIdAndStatus(userId, Status.REJECTED);

        return new ProjectListResDto(projects.stream()
                .map(project -> projectMapper.toRes(project, false))
                .toList());
    }
}
