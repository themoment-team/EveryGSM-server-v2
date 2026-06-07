package team.themoment.everygsm.server.v2.domain.project.service;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.project.dto.request.UpdateParticipantsReqDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.ProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.entity.constant.Status;
import team.themoment.everygsm.server.v2.domain.project.mapper.ProjectMapper;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectLikeRepository;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectRepository;
import team.themoment.everygsm.server.v2.domain.project.service.util.ParticipantResolver;
import team.themoment.everygsm.server.v2.domain.user.entity.UserJpaEntity;
import team.themoment.everygsm.server.v2.global.exception.error.ExpectedException;

@Service
@RequiredArgsConstructor
public class UpdateProjectParticipantsService {

    private final ProjectRepository projectRepository;
    private final ProjectLikeRepository projectLikeRepository;
    private final ProjectMapper projectMapper;
    private final ParticipantResolver participantResolver;

    @Transactional
    public ProjectResDto execute(Long userId, Long projectId, UpdateParticipantsReqDto reqDto) {
        ProjectJpaEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ExpectedException("해당 프로젝트가 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        UserJpaEntity owner = project.getUser();
        if (!owner.getId().equals(userId)) {
            throw new ExpectedException("프로젝트 참여자를 수정할 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        Status status = project.getStatus();
        if (status != Status.PENDING && status != Status.APPROVED) {
            throw new ExpectedException("현재 상태에서는 참여자를 수정할 수 없습니다.", HttpStatus.BAD_REQUEST);
        }

        Set<UserJpaEntity> newParticipants = participantResolver.resolve(reqDto.participantIds(), owner);
        project.replaceParticipants(newParticipants);

        boolean liked = projectLikeRepository.existsByProjectIdAndUserId(projectId, userId);
        return projectMapper.toRes(project, liked);
    }
}
