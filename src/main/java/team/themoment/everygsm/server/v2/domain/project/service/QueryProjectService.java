package team.themoment.everygsm.server.v2.domain.project.service;

import static team.themoment.everygsm.server.v2.domain.project.entity.constant.Status.APPROVED;

import java.util.List;
import java.util.Set;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.project.dto.response.ProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.QueryProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.mapper.ProjectMapper;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectLikeRepository;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectRepository;

@Service
@RequiredArgsConstructor
public class QueryProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectLikeRepository projectLikeRepository;
    private final ProjectMapper projectMapper;

    @Transactional
    public QueryProjectResDto execute(@Nullable Long userId) {
        return buildQueryResDto(userId);
    }

    private QueryProjectResDto buildQueryResDto(@Nullable Long userId) {
        if (userId == null) {
            return buildGuestQuery();
        }
        return buildUserQuery(userId);
    }

    private QueryProjectResDto buildGuestQuery() {
        List<ProjectJpaEntity> projects = projectRepository.findByStatus(APPROVED);
        List<ProjectResDto> res = projects.stream().map(p -> projectMapper.toRes(p,false)).toList();

        return new QueryProjectResDto(res);
    }

    private QueryProjectResDto buildUserQuery(Long userId) {
        List<ProjectJpaEntity> projects = projectRepository.findByStatus(APPROVED);

        List<Long> projectIds = projects.stream().map(ProjectJpaEntity::getId).toList();

        Set<Long> likedProjectIds = new java.util.HashSet<>(projectLikeRepository.findByProjectId(userId, projectIds));

        List<ProjectResDto> res = projects.stream()
                .map(p -> projectMapper.toRes(p, likedProjectIds.contains(p.getId()))).toList();

        return new QueryProjectResDto(res);
    }
}
