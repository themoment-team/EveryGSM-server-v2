package team.themoment.everygsm.server.v2.domain.project.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import team.themoment.everygsm.server.v2.domain.project.dto.common.RepositoryDto;
import team.themoment.everygsm.server.v2.domain.project.dto.common.TechStackDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.ProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;

@Component
public class ProjectMapper {

    public ProjectResDto toRes(ProjectJpaEntity project) {
        List<TechStackDto> techStacks = extractTechStacks(project);
        List<RepositoryDto> repositories = extractRepositories(project);

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

    public ProjectResDto toUserRes(ProjectJpaEntity project, boolean liked) {
        List<TechStackDto> techStacks = extractTechStacks(project);
        List<RepositoryDto> repositories = extractRepositories(project);

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
                liked);
    }

    public List<TechStackDto> extractTechStacks(ProjectJpaEntity project) {
        return project.getStackNames().stream().map(TechStackDto::new).toList();
    }

    public List<RepositoryDto> extractRepositories(ProjectJpaEntity project) {
        return project.getRepoUrls().stream().map(RepositoryDto::new).toList();
    }
}
