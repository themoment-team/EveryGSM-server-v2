package team.themoment.everygsm.server.v2.domain.project.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.project.dto.common.RepositoryDto;
import team.themoment.everygsm.server.v2.domain.project.dto.common.TechStackDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.MyPageResDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.ProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectRepository;

@Service
@RequiredArgsConstructor
public class QueryMypageService {
    private final ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    public MyPageResDto execute(Long userId) {
        List<ProjectJpaEntity> likedProjects = projectRepository.findLikedProjectsByUserId(userId);
        Set<Long> likedProjectIds = likedProjects.stream().map(ProjectJpaEntity::getId).collect(Collectors.toSet());

        // 좋아요한 프로젝트
        List<ProjectResDto> liked = likedProjects.stream()
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
                        project.getRepoUrls().stream().map(r -> new RepositoryDto(r.getRepoName(), r.getRepoUrl())).toList(),
                        true // 좋아요가 되어있는 것만 조회했기 때문에 true
                )).toList();

        // 내가 등록한 프로젝트
        List<ProjectJpaEntity> registered = projectRepository.findRegisteredProjectsByUserId(userId);

        List<ProjectResDto> registeredDtos = registered.stream()
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
                        project.getRepoUrls().stream().map(r -> new RepositoryDto(r.getRepoName(), r.getRepoUrl())).toList(),
                        likedProjectIds.contains(project.getId()) // 좋아요 여부 설정
                )).toList();

        return new MyPageResDto(liked, registeredDtos);
    }
}
