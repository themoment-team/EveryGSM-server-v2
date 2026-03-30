package team.themoment.everygsm.server.v2.domain.project.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.project.dto.response.MyPageResDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.ProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.entity.constant.Status;
import team.themoment.everygsm.server.v2.domain.project.mapper.ProjectMapper;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectRepository;

@Service
@RequiredArgsConstructor
public class QueryMypageService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Transactional(readOnly = true)
    public MyPageResDto execute(Long userId) {
        List<ProjectJpaEntity> likedProjects = projectRepository.findLikedProjectsByUserId(userId);
        Set<Long> likedProjectIds = likedProjects.stream().map(ProjectJpaEntity::getId).collect(Collectors.toSet());

        // 좋아요한 프로젝트
        List<ProjectResDto> liked = likedProjects.stream().map(project -> projectMapper.toRes(project, true)).toList();

        // 내가 등록한 프로젝트
        List<ProjectJpaEntity> registered = projectRepository.findByUserIdAndStatus(userId, Status.APPROVED);

        List<ProjectResDto> registeredDtos = registered.stream()
                .map(project -> projectMapper.toRes(project, likedProjectIds.contains(project.getId()))).toList();

        return new MyPageResDto(liked, registeredDtos);
    }
}
