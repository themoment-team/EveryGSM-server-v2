package team.themoment.everygsm.server.v2.domain.project.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import team.themoment.datagsm.sdk.openapi.DataGsmOpenApiClient;
import team.themoment.datagsm.sdk.openapi.client.ProjectApi;
import team.themoment.datagsm.sdk.openapi.model.ClubDetail;
import team.themoment.datagsm.sdk.openapi.model.ParticipantInfo;
import team.themoment.datagsm.sdk.openapi.model.Project;
import team.themoment.datagsm.sdk.openapi.model.ProjectResponse;
import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.entity.constant.Status;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectRepository;
import team.themoment.everygsm.server.v2.domain.user.entity.UserJpaEntity;
import team.themoment.everygsm.server.v2.domain.user.entity.constant.Role;
import team.themoment.everygsm.server.v2.domain.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyncProjectService {

    private final DataGsmOpenApiClient dataGsmOpenApiClient;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Autowired
    @Lazy
    private SyncProjectService self;

    public void execute() {
        Optional<List<Project>> fetched = fetchAllProjects();
        Set<Long> seenExternalIds = new HashSet<>();

        for (Project externalProject : fetched.orElseGet(List::of)) {
            seenExternalIds.add(externalProject.getId());
            try {
                self.syncProject(externalProject);
            } catch (RuntimeException e) {
                log.error("Failed to sync project id={}", externalProject.getId(), e);
            }
        }

        // 목록을 일부만 가져온 상태에서 비활성화를 수행하면, 조회하지 못한 페이지의
        // 정상 프로젝트까지 INACTIVE로 바뀌므로 전체 조회에 성공했을 때만 수행한다.
        if (fetched.isEmpty()) {
            log.warn("datagsm 프로젝트 전체 조회에 실패해 미조회 프로젝트 비활성화를 건너뜁니다.");
            return;
        }

        self.markUnseenInactive(seenExternalIds);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void syncProject(Project externalProject) {
        String affiliation = externalProject.getClub().map(club -> club.getName()).orElse(null);
        UserJpaEntity owner = findOrCreateOwner(externalProject);

        ProjectJpaEntity matched = projectRepository.findByExternalProjectId(externalProject.getId())
                .orElseGet(() -> healOrphanedProject(externalProject));

        if (matched == null) {
            projectRepository.save(buildNewProject(externalProject, affiliation, owner));
            return;
        }

        matched.syncUpdate(externalProject
                .getName(), externalProject.getDescription(), affiliation, externalProject.getStartYear(), owner);
    }

    /**
     * 승인 시 datagsm 등록은 성공했으나 external_project_id 저장이 유실된 프로젝트를 찾아 id를 다시 연결한다. 이
     * 복구가 없으면 동기화가 해당 프로젝트를 신규로 판단해 중복 행을 만든다.
     */
    private ProjectJpaEntity healOrphanedProject(Project externalProject) {
        List<ProjectJpaEntity> candidates = projectRepository
                .findByExternalProjectIdIsNullAndTitleAndStartYearAndStatusNot(externalProject.getName(),
                        externalProject.getStartYear(),
                        Status.INACTIVE);

        if (candidates.size() != 1) {
            if (candidates.size() > 1) {
                log.warn("external_project_id 복구 후보가 여러 건이라 건너뜁니다. title={}, startYear={}, count={}",
                        externalProject.getName(),
                        externalProject.getStartYear(),
                        candidates.size());
            }
            return null;
        }

        ProjectJpaEntity orphan = candidates.getFirst();
        orphan.assignExternalProjectId(externalProject.getId());
        log.info("유실된 external_project_id를 복구했습니다. projectId={}, externalProjectId={}",
                orphan.getId(),
                externalProject.getId());
        return orphan;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markUnseenInactive(Set<Long> seenExternalIds) {
        projectRepository.markUnseenProjectsAsInactive(seenExternalIds);
    }

    /**
     * 전체 페이지를 모두 가져오지 못하면 비어있는 Optional을 반환한다. 호출부가 부분 결과로 비활성화를 수행하지 않도록 하기 위함이다.
     */
    private Optional<List<Project>> fetchAllProjects() {
        List<Project> all = new ArrayList<>();
        int page = 0;
        int totalPages;
        do {
            ProjectResponse response = dataGsmOpenApiClient.projects()
                    .getProjects(new ProjectApi.ProjectRequest().page(page).size(100));
            if (response == null || response.getProjects() == null || response.getTotalPages() == null) {
                log.warn("datagsm 프로젝트 목록 응답이 비어있어 조회를 중단합니다. page={}", page);
                return Optional.empty();
            }
            all.addAll(response.getProjects());
            totalPages = response.getTotalPages();
            page++;
        } while (page < totalPages);
        return Optional.of(all);
    }

    private ProjectJpaEntity buildNewProject(Project externalProject, String affiliation, UserJpaEntity owner) {
        return ProjectJpaEntity.builder().user(owner).title(externalProject.getName())
                .description(externalProject.getDescription()).affiliation(affiliation).logo("").prodUrl("")
                .startYear(externalProject.getStartYear()).status(Status.APPROVED).stackNames(new HashSet<>())
                .externalProjectId(externalProject.getId()).build();
    }

    private UserJpaEntity findOrCreateOwner(Project externalProject) {
        if (externalProject.getClub().isPresent()) {
            long clubId = externalProject.getClub().get().getId();
            try {
                ClubDetail clubDetail = dataGsmOpenApiClient.clubs().getClub(clubId);
                if (clubDetail.getLeader().isPresent()) {
                    return findOrCreateUser(clubDetail.getLeader().get());
                }
            } catch (RuntimeException e) {
                log.warn("Failed to fetch club leader for clubId={}, falling back to project participants", clubId, e);
            }
        }

        List<ParticipantInfo> participants = externalProject.getParticipants();
        if (participants != null && !participants.isEmpty()) {
            return findOrCreateUser(participants.getFirst());
        }

        log.warn("No leader or participants available for project id={}, owner will be null", externalProject.getId());
        return null;
    }

    private UserJpaEntity findOrCreateUser(ParticipantInfo participant) {
        return userRepository.findByEmail(participant.getEmail())
                .orElseGet(() -> userRepository.save(UserJpaEntity.builder().email(participant.getEmail())
                        .name(participant.getName()).studentNumber(String.valueOf(participant.getStudentNumber()))
                        .role(Role.USER).build()));
    }
}
