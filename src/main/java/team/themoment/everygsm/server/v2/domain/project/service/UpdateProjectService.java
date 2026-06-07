package team.themoment.everygsm.server.v2.domain.project.service;

import static team.themoment.everygsm.server.v2.domain.project.entity.constant.Status.PENDING;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.project.dto.common.TechStackDto;
import team.themoment.everygsm.server.v2.domain.project.dto.request.UpdateProjectReqDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.ProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.mapper.ProjectMapper;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectLikeRepository;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectRepository;
import team.themoment.everygsm.server.v2.domain.project.service.util.ParticipantResolver;
import team.themoment.everygsm.server.v2.domain.user.entity.UserJpaEntity;
import team.themoment.everygsm.server.v2.global.exception.error.ExpectedException;

@Service
@RequiredArgsConstructor
public class UpdateProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectLikeRepository projectLikeRepository;
    private final ProjectMapper projectMapper;
    private final ParticipantResolver participantResolver;

    @Transactional
    public ProjectResDto execute(Long userId, Long projectId, UpdateProjectReqDto reqDto) {
        ProjectJpaEntity project = projectRepository.findProjectWithCollectionsByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ExpectedException("프로젝트를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        UserJpaEntity owner = project.getUser();
        Set<String> stackNames = extractStackNames(reqDto);
        Set<String> repoUrls = extractRepoUrls(reqDto);
        Set<UserJpaEntity> participants = participantResolver.resolve(reqDto.participantIds(), owner);

        return switch (project.getStatus()) {
            case INACTIVE -> throw new ExpectedException("비활성화된 프로젝트는 수정할 수 없습니다.", HttpStatus.BAD_REQUEST);
            case PENDING, REJECTED -> {
                project.updateContent(reqDto.logo(),
                        reqDto.title(),
                        reqDto.affiliation(),
                        reqDto.description(),
                        reqDto.prodUrl(),
                        reqDto.startYear(),
                        stackNames,
                        repoUrls,
                        participants);
                boolean liked = projectLikeRepository.existsByProjectIdAndUserId(projectId, userId);
                yield projectMapper.toRes(project, liked);
            }
            case APPROVED -> {
                ProjectJpaEntity copy = projectRepository.findByOriginalProjectIdAndStatus(projectId, PENDING)
                        .orElseGet(() -> projectRepository.save(buildCopy(project, owner)));
                copy.updateContent(reqDto.logo(),
                        reqDto.title(),
                        reqDto.affiliation(),
                        reqDto.description(),
                        reqDto.prodUrl(),
                        reqDto.startYear(),
                        stackNames,
                        repoUrls,
                        participants);
                yield projectMapper.toRes(copy, false);
            }
        };
    }

    private ProjectJpaEntity buildCopy(ProjectJpaEntity original, UserJpaEntity owner) {
        return ProjectJpaEntity.builder().user(owner).logo(original.getLogo()).title(original.getTitle())
                .affiliation(original.getAffiliation()).description(original.getDescription())
                .prodUrl(original.getProdUrl()).startYear(original.getStartYear()).status(PENDING)
                .stackNames(new HashSet<>(original.getStackNames())).repoUrls(new HashSet<>(original.getRepoUrls()))
                .participants(new HashSet<>(original.getParticipants())).originalProjectId(original.getId()).build();
    }

    private Set<String> extractStackNames(UpdateProjectReqDto reqDto) {
        return Optional.ofNullable(reqDto.techStack()).stream().flatMap(Collection::stream).map(TechStackDto::stackName)
                .collect(Collectors.toSet());
    }

    private Set<String> extractRepoUrls(UpdateProjectReqDto reqDto) {
        return Optional.ofNullable(reqDto.repository()).stream().flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }
}
