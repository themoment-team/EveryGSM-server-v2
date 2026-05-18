package team.themoment.everygsm.server.v2.domain.project.service;

import static team.themoment.everygsm.server.v2.domain.project.entity.constant.Status.PENDING;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.project.dto.common.TechStackDto;
import team.themoment.everygsm.server.v2.domain.project.dto.request.CreateProjectReqDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.ProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.event.ProjectRegisteredEvent;
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
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ProjectResDto execute(Long userId, CreateProjectReqDto reqDto) {
        UserJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExpectedException("해당 유저가 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        ProjectJpaEntity project = projectRepository.save(buildProject(reqDto, user));

        eventPublisher.publishEvent(new ProjectRegisteredEvent(project.getTitle(),
                project.getAffiliation(),
                project.getDescription(),
                project.getStartYear(),
                project.getProdUrl(),
                project.getStackNames(),
                project.getRepoUrls(),
                user.getName(),
                user.getStudentNumber(),
                user.getEmail()));

        return projectMapper.toRes(project, false);
    }

    private ProjectJpaEntity buildProject(CreateProjectReqDto reqDto, UserJpaEntity user) {
        Set<String> stackNames = Optional.ofNullable(reqDto.techStack()).stream().flatMap(Collection::stream)
                .map(TechStackDto::stackName).collect(Collectors.toSet());

        Set<String> repoUrls = Optional.ofNullable(reqDto.repository()).stream().flatMap(Collection::stream)
                .collect(Collectors.toSet());

        return ProjectJpaEntity.builder().user(user).logo(reqDto.logo()).title(reqDto.title())
                .affiliation(reqDto.affiliation()).description(reqDto.description()).prodUrl(reqDto.prodUrl())
                .startYear(reqDto.startYear()).status(PENDING).stackNames(stackNames).repoUrls(repoUrls).build();
    }
}
