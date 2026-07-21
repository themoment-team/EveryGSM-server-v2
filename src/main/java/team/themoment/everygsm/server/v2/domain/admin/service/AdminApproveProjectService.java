package team.themoment.everygsm.server.v2.domain.admin.service;

import static team.themoment.everygsm.server.v2.domain.project.entity.constant.Status.APPROVED;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import team.themoment.datagsm.sdk.openapi.DataGsmOpenApiClient;
import team.themoment.datagsm.sdk.openapi.client.ProjectApi;
import team.themoment.datagsm.sdk.openapi.model.Project;
import team.themoment.datagsm.sdk.openapi.model.ProjectResponse;
import team.themoment.everygsm.server.v2.domain.project.dto.response.ProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.mapper.ProjectMapper;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectRepository;
import team.themoment.everygsm.server.v2.global.exception.error.ExpectedException;
import team.themoment.everygsm.server.v2.global.thirdparty.feign.datagsm.DatagsmApiClient;
import team.themoment.everygsm.server.v2.global.thirdparty.feign.datagsm.dto.ClubListResDto;
import team.themoment.everygsm.server.v2.global.thirdparty.feign.datagsm.dto.DatagsmApiResponse;
import team.themoment.everygsm.server.v2.global.thirdparty.feign.datagsm.dto.DatagsmProjectResDto;
import team.themoment.everygsm.server.v2.global.thirdparty.feign.datagsm.dto.ProjectReqDto;
import team.themoment.everygsm.server.v2.global.thirdparty.feign.datagsm.dto.QueryClubReqDto;
import team.themoment.everygsm.server.v2.global.thirdparty.feign.datagsm.dto.QueryStudentReqDto;
import team.themoment.everygsm.server.v2.global.thirdparty.feign.datagsm.dto.StudentListResDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminApproveProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final DatagsmApiClient datagsmApiClient;
    private final DataGsmOpenApiClient dataGsmOpenApiClient;

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
        // datagsm에는 프로젝트 update API가 없으므로, 이미 같은 이름이 등록돼 있으면 그 id를 매핑하고 생성을 생략한다.
        Long existingId = findExistingExternalId(project.getTitle(), project.getStartYear());
        if (existingId != null) {
            assignIfUnoccupied(project, existingId);
            return;
        }

        Long clubId = resolveClubId(project.getAffiliation());
        List<Long> participantIds = resolveParticipantIds(project);

        ProjectReqDto reqDto = ProjectReqDto.builder().name(project.getTitle()).description(project.getDescription())
                .startYear(project.getStartYear()).clubId(clubId).participantIds(participantIds).build();

        DatagsmApiResponse<DatagsmProjectResDto> response = datagsmApiClient.createProject(reqDto);
        if (response == null || response.getData() == null || response.getData().getId() == null) {
            // datagsm에는 이미 생성됐는데 응답만 못 읽었을 수 있다. 여기서 id를 회수하지 못하면
            // 트랜잭션이 롤백되며 external_project_id가 유실되고, 다음 동기화가 중복 행을 만든다.
            Long recoveredId = findExistingExternalId(project.getTitle(), project.getStartYear());
            if (recoveredId != null) {
                log.warn("datagsm 등록 응답을 읽지 못했으나 이름 조회로 id를 회수했습니다. title={}, externalProjectId={}",
                        project.getTitle(),
                        recoveredId);
                assignIfUnoccupied(project, recoveredId);
                return;
            }

            String cause = (response != null && response.getMessage() != null)
                    ? response.getMessage()
                    : "응답 바디가 비어있거나 id가 누락되었습니다.";
            throw new ExpectedException("datagsm 프로젝트 등록에 실패했습니다. 원인=" + cause + ", title=" + project.getTitle(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        assignIfUnoccupied(project, response.getData().getId());
    }

    /**
     * external_project_id는 unique 제약이 걸려 있어, 다른 프로젝트가 선점한 id를 할당하면 커밋 시점에야 실패한다.
     * 원인이 드러나도록 미리 검증한다.
     */
    private void assignIfUnoccupied(ProjectJpaEntity project, Long externalProjectId) {
        projectRepository.findByExternalProjectId(externalProjectId)
                .filter(owner -> !owner.getId().equals(project.getId())).ifPresent(owner -> {
                    throw new ExpectedException(
                            "이미 다른 프로젝트에 매핑된 datagsm 프로젝트입니다. externalProjectId=" + externalProjectId
                                    + ", 점유 중인 projectId=" + owner.getId(),
                            HttpStatus.CONFLICT);
                });
        project.assignExternalProjectId(externalProjectId);
    }

    private Long findExistingExternalId(String title, Integer startYear) {
        int page = 0;
        int totalPages;
        do {
            ProjectResponse response = dataGsmOpenApiClient.projects()
                    .getProjects(new ProjectApi.ProjectRequest().projectName(title).page(page).size(100));
            if (response == null || response.getProjects() == null) {
                return null;
            }
            Long matched = response.getProjects().stream()
                    .filter(p -> title.equals(p.getName()) && Objects.equals(startYear, p.getStartYear()))
                    .map(Project::getId).findFirst().orElse(null);
            if (matched != null) {
                return matched;
            }
            totalPages = response.getTotalPages();
            page++;
        } while (page < totalPages);
        return null;
    }

    private Long resolveClubId(String affiliation) {
        if (affiliation == null || affiliation.isBlank()) {
            return null;
        }
        ClubListResDto res = datagsmApiClient.getClubs(QueryClubReqDto.builder().clubName(affiliation).build());
        if (res == null || res.getClubs() == null || res.getClubs().isEmpty()) {
            return null;
        }
        return res.getClubs().getFirst().getId();
    }

    private List<Long> resolveParticipantIds(ProjectJpaEntity project) {
        Set<String> emails = new LinkedHashSet<>();
        if (project.getUser() != null) {
            emails.add(project.getUser().getEmail());
        }
        if (project.getParticipants() != null) {
            project.getParticipants().forEach(participant -> emails.add(participant.getEmail()));
        }

        List<Long> studentIds = new ArrayList<>();
        for (String email : emails) {
            Long studentId = resolveStudentId(email);
            if (studentId != null) {
                studentIds.add(studentId);
            } else {
                log.warn("datagsm에서 참여자 학생을 찾지 못해 동기화에서 제외합니다. email={}, projectId={}", email, project.getId());
            }
        }
        return studentIds;
    }

    private Long resolveStudentId(String email) {
        StudentListResDto res = datagsmApiClient.getStudents(QueryStudentReqDto.builder().email(email).build());
        if (res == null || res.getStudents() == null || res.getStudents().isEmpty()) {
            return null;
        }
        return res.getStudents().get(0).getId();
    }
}
