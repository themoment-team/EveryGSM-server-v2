package team.themoment.everygsm.server.v2.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.everygsm.server.v2.domain.project.dto.common.RepositoryDto;
import team.themoment.everygsm.server.v2.domain.project.dto.common.TechStackDto;
import team.themoment.everygsm.server.v2.domain.project.dto.request.CreateProjectReqDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.ProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectRepository;
import team.themoment.everygsm.server.v2.domain.user.entity.UserJpaEntity;
import team.themoment.everygsm.server.v2.domain.user.repository.UserRepository;
import team.themoment.everygsm.server.v2.global.exception.error.ExpectedException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static team.themoment.everygsm.server.v2.domain.project.entity.constant.Status.PENDING;

@Service
@RequiredArgsConstructor
public class CreateProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProjectResDto execute(Long userId, CreateProjectReqDto reqDto){
        UserJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExpectedException("해당 유저가 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        ProjectJpaEntity project = projectRepository.save(buildProject(reqDto, user));
        return buildProjectResDto(project);
    }

    private ProjectJpaEntity buildProject(CreateProjectReqDto reqDto, UserJpaEntity user){
        Set<String> repoUrls = java.util.Optional.ofNullable(reqDto.repository()).stream()
                .flatMap(java.util.Collection::stream)
                .map(RepositoryDto::repoUrl)
                .collect(Collectors.toSet());

        Set<String> stackNames = java.util.Optional.ofNullable(reqDto.techStack()).stream()
                .flatMap(java.util.Collection::stream)
                .map(TechStackDto::stackName)
                .collect(Collectors.toSet());

        return ProjectJpaEntity.builder()
                .user(user)
                .logo(reqDto.logo())
                .title(reqDto.title())
                .affiliation(reqDto.affiliation())
                .description(reqDto.description())
                .prodUrl(reqDto.prodUrl())
                .status(PENDING)
                .repoUrls(repoUrls)
                .stackNames(stackNames)
                .build();
    }
    private ProjectResDto buildProjectResDto(ProjectJpaEntity project){
        List<TechStackDto> techStacks =
                project.getStackNames().stream()
                        .map(TechStackDto::new)
                        .toList();

        List<RepositoryDto> repositories =
                project.getRepoUrls().stream()
                        .map(RepositoryDto::new)
                        .toList();

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
                techStacks,
                repositories,
                false
        );
    }
}
