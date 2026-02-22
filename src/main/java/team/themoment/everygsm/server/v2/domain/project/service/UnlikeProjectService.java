package team.themoment.everygsm.server.v2.domain.project.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.project.entity.LikeJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectLikeRepository;
import team.themoment.everygsm.server.v2.global.exception.error.ExpectedException;

@Service
@RequiredArgsConstructor
public class UnlikeProjectService {

    private final ProjectLikeRepository projectLikeRepository;

    @Transactional
    public void execute(Long userId, Long projectId) {
        LikeJpaEntity like = projectLikeRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new ExpectedException("좋아요한 프로젝트가 아닙니다.", HttpStatus.NOT_FOUND));

        projectLikeRepository.delete(like);
    }
}
