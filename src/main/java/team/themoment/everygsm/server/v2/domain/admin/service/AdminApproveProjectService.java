package team.themoment.everygsm.server.v2.domain.admin.service;

import static team.themoment.everygsm.server.v2.domain.project.entity.constant.Status.APPROVED;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.project.dto.response.ProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.mapper.ProjectMapper;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectRepository;
import team.themoment.everygsm.server.v2.global.exception.error.ExpectedException;
import team.themoment.everygsm.server.v2.global.thirdparty.feign.datagsm.DatagsmApiClient;
import team.themoment.everygsm.server.v2.global.thirdparty.feign.datagsm.dto.ClubListResDto;
import team.themoment.everygsm.server.v2.global.thirdparty.feign.datagsm.dto.ProjectReqDto;
import team.themoment.everygsm.server.v2.global.thirdparty.feign.datagsm.dto.QueryClubReqDto;
import team.themoment.everygsm.server.v2.global.thirdparty.feign.datagsm.dto.QueryStudentReqDto;
import team.themoment.everygsm.server.v2.global.thirdparty.feign.datagsm.dto.StudentListResDto;

@Service
@RequiredArgsConstructor
public class AdminApproveProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final DatagsmApiClient datagsmApiClient;

    @Transactional
    public ProjectResDto execute(Long projectId) {
        ProjectJpaEntity project = projectRepository.findProjectWithCollectionsById(projectId)
                .orElseThrow(() -> new ExpectedException("해당 프로젝트가 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        if (project.getOriginalProjectId() != null) {
            ProjectJpaEntity original = projectRepository.findProjectWithCollectionsById(project.getOriginalProjectId())
                    .orElseThrow(() -> new ExpectedException("원본 프로젝트가 존재하지 않습니다.", HttpStatus.NOT_FOUND));
            original.applyFrom(project);
            if (original.getExternalProjectId() == null) {
                registerToDatagsm(original);
            }
            original.updateStatus(APPROVED, null);
            project.markInactive();
            return projectMapper.toRes(original, false);
        }

        if (project.getExternalProjectId() == null) {
            registerToDatagsm(project);
        }

        project.updateStatus(APPROVED, null);
        return projectMapper.toRes(project, false);
    }

    private void registerToDatagsm(ProjectJpaEntity project) {
        Long clubId = resolveClubId(project.getAffiliation());
        List<Long> participantIds = project.getUser() != null
                ? resolveParticipantIds(project.getUser().getEmail())
                : List.of();

        ProjectReqDto reqDto = ProjectReqDto.builder().name(project.getTitle()).description(project.getDescription())
                .startYear(project.getStartYear()).clubId(clubId).participantIds(participantIds).build();

        Long externalProjectId = datagsmApiClient.createProject(reqDto).getId();
        project.assignExternalProjectId(externalProjectId);
    }

    private Long resolveClubId(String affiliation) {
        if (affiliation == null || affiliation.isBlank()) {
            return null;
        }
        ClubListResDto res = datagsmApiClient.getClubs(QueryClubReqDto.builder().clubName(affiliation).build());
        if (res.getClubs() == null || res.getClubs().isEmpty()) {
            return null;
        }
        return res.getClubs().get(0).getId();
    }

    private List<Long> resolveParticipantIds(String email) {
        StudentListResDto res = datagsmApiClient.getStudents(QueryStudentReqDto.builder().email(email).build());
        if (res.getStudents() == null || res.getStudents().isEmpty()) {
            return List.of();
        }
        return List.of(res.getStudents().get(0).getId());
    }
}
