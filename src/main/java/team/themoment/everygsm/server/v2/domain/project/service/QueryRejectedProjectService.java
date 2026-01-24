package team.themoment.everygsm.server.v2.domain.project.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.project.dto.common.RepositoryDto;
import team.themoment.everygsm.server.v2.domain.project.dto.common.TechStackDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.ProjectListResDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.ProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.entity.constant.Status;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectRepository;

@Service
@RequiredArgsConstructor
public class QueryRejectedProjectService {
    private final ProjectRepository projectRepository;

    public ProjectListResDto execute(Long userId) {
        List<ProjectJpaEntity> projects = projectRepository.findByUserIdAndStatus(userId, Status.REJECTED);

        return new ProjectListResDto(projects.stream()
                .map(project -> new ProjectResDto(project.getId(),
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
                        false // 거절된 프로젝트는 좋아요 불가
                )).toList());
    }
}
