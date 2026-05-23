package team.themoment.everygsm.server.v2.domain.project.service;

import static team.themoment.everygsm.server.v2.domain.project.entity.constant.Status.APPROVED;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.project.dto.response.ProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.QueryProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.entity.constant.ProjectSortType;
import team.themoment.everygsm.server.v2.domain.project.mapper.ProjectMapper;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectLikeRepository;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectRepository;

@Service
@RequiredArgsConstructor
public class QueryProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectLikeRepository projectLikeRepository;
    private final ProjectMapper projectMapper;

    @Transactional(readOnly = true)
    public QueryProjectResDto execute(@Nullable Long userId, ProjectSortType sortType) {
        return buildQueryResDto(userId, sortType);
    }

    private QueryProjectResDto buildQueryResDto(@Nullable Long userId, ProjectSortType sortType) {
        if (userId == null) {
            return buildGuestQuery(sortType);
        }
        return buildUserQuery(userId, sortType);
    }

    private QueryProjectResDto buildGuestQuery(ProjectSortType sortType) {
        List<ProjectJpaEntity> projects = projectRepository.findAllByStatusWithCollections(APPROVED, sortType);
        projects = applySortIfNeeded(projects, sortType);

        List<ProjectResDto> res = projects.stream().map(p -> projectMapper.toRes(p, false)).toList();
        return new QueryProjectResDto(res);
    }

    private QueryProjectResDto buildUserQuery(Long userId, ProjectSortType sortType) {
        List<ProjectJpaEntity> projects = projectRepository.findAllByStatusWithCollections(APPROVED, sortType);
        projects = applySortIfNeeded(projects, sortType);

        List<Long> projectIds = projects.stream().map(ProjectJpaEntity::getId).toList();
        Set<Long> likedProjectIds = new java.util.HashSet<>(projectLikeRepository.findByProjectId(userId, projectIds));

        List<ProjectResDto> res = projects.stream()
                .map(p -> projectMapper.toRes(p, likedProjectIds.contains(p.getId()))).toList();

        return new QueryProjectResDto(res);
    }

    private List<ProjectJpaEntity> applySortIfNeeded(List<ProjectJpaEntity> projects, ProjectSortType sortType) {
        if (sortType != ProjectSortType.LIKES) {
            return projects;
        }

        List<Long> projectIds = projects.stream().map(ProjectJpaEntity::getId).toList();

        Map<Long, Long> likeCountMap = projectLikeRepository.countByProjectIds(projectIds).stream()
                .collect(Collectors.toMap(p -> p.getProjectId(), p -> p.getLikeCount()));

        return projects
                .stream().sorted(Comparator
                        .comparingLong((ProjectJpaEntity p) -> likeCountMap.getOrDefault(p.getId(), 0L)).reversed())
                .toList();
    }
}
