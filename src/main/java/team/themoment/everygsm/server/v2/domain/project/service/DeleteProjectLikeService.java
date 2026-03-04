package team.themoment.everygsm.server.v2.domain.project.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.project.dto.response.ProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.entity.constant.Status;
import team.themoment.everygsm.server.v2.domain.project.mapper.ProjectMapper;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectLikeRepository;
import team.themoment.everygsm.server.v2.global.exception.error.ExpectedException;

@Service
@RequiredArgsConstructor
public class DeleteProjectLikeService {

    private final ProjectLikeRepository projectLikeRepository;
    private final ProjectMapper projectMapper;

    @Transactional
    public ProjectResDto execute(Long userId, Long projectId) {
        ProjectJpaEntity project = projectLikeRepository
                .findProjectWithCollectionsByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new ExpectedException("좋아요한 프로젝트가 아닙니다.", HttpStatus.NOT_FOUND));

        if (project.getStatus() != Status.APPROVED) {
            throw new ExpectedException("승인된 프로젝트가 아닙니다.", HttpStatus.FORBIDDEN);
        }

        projectLikeRepository.deleteByUserIdAndProjectId(userId, projectId);

        return projectMapper.toRes(project, false);
    }
}