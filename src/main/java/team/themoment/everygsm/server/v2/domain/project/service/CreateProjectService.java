package team.themoment.everygsm.server.v2.domain.project.service;

import static team.themoment.everygsm.server.v2.domain.project.entity.constant.Status.PENDING;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.project.dto.common.RepositoryDto;
import team.themoment.everygsm.server.v2.domain.project.dto.common.TechStackDto;
import team.themoment.everygsm.server.v2.domain.project.dto.request.CreateProjectReqDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.ProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.mapper.ProjectMapper;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectRepository;
import team.themoment.everygsm.server.v2.domain.user.entity.UserJpaEntity;
import team.themoment.everygsm.server.v2.domain.user.repository.UserRepository;
import team.themoment.everygsm.server.v2.global.exception.error.ExpectedException;

@Service
@RequiredArgsConstructor
public class CreateProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;

    @Transactional
    public ProjectResDto execute(Long userId, CreateProjectReqDto reqDto) {
        UserJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExpectedException("해당 유저가 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        ProjectJpaEntity project = projectRepository.save(buildProject(reqDto, user));
        return projectMapper.toRes(project, false);
    }

    private ProjectJpaEntity buildProject(CreateProjectReqDto reqDto, UserJpaEntity user) {
        Set<String> repoUrls = java.util.Optional.ofNullable(reqDto.repository()).stream()
                .flatMap(java.util.Collection::stream).map(RepositoryDto::repoUrl).collect(Collectors.toSet());

        Set<String> stackNames = java.util.Optional.ofNullable(reqDto.techStack()).stream()
                .flatMap(java.util.Collection::stream).map(TechStackDto::stackName).collect(Collectors.toSet());

        return ProjectJpaEntity.builder().user(user).logo(reqDto.logo()).title(reqDto.title())
                .affiliation(reqDto.affiliation()).description(reqDto.description()).prodUrl(reqDto.prodUrl())
                .status(PENDING).repoUrls(repoUrls).stackNames(stackNames).build();
    }
}
