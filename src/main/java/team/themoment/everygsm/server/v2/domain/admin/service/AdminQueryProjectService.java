package team.themoment.everygsm.server.v2.domain.admin.service;

import static team.themoment.everygsm.server.v2.domain.project.entity.constant.Status.PENDING;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.project.dto.response.ProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.QueryProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.mapper.ProjectMapper;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectRepository;

@Service
@RequiredArgsConstructor
public class AdminQueryProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Transactional(readOnly = true)
    public QueryProjectResDto execute() {
        return buildAdminQueryResDto();
    }

    private QueryProjectResDto buildAdminQueryResDto() {
        List<ProjectJpaEntity> projects = projectRepository.findByStatus(PENDING);
        List<ProjectResDto> res = projects.stream().map(p -> projectMapper.toRes(p, false)).toList();

        return new QueryProjectResDto(res);
    }

}
