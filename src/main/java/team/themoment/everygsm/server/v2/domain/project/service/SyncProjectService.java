package team.themoment.everygsm.server.v2.domain.project.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import team.themoment.datagsm.sdk.openapi.DataGsmOpenApiClient;
import team.themoment.datagsm.sdk.openapi.client.ClubApi;
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

    @Transactional
    public void execute() {
        List<Project> allExternalProjects = fetchAllProjects();
        Set<Long> seenExternalIds = new HashSet<>();

        for (Project externalProject : allExternalProjects) {
            try {
                syncProject(externalProject);
                seenExternalIds.add(externalProject.getId());
            } catch (Exception e) {
                log.error("Failed to sync project id={}", externalProject.getId(), e);
            }
        }

        projectRepository.findAllByExternalProjectIdIsNotNull().stream()
                .filter(p -> !seenExternalIds.contains(p.getExternalProjectId()))
                .forEach(ProjectJpaEntity::markInactive);
    }

    private List<Project> fetchAllProjects() {
        List<Project> all = new ArrayList<>();
        int page = 0;
        int totalPages;
        do {
            ProjectResponse response = dataGsmOpenApiClient.projects()
                    .getProjects(new ProjectApi.ProjectRequest().page(page).size(100));
            all.addAll(response.getProjects());
            totalPages = response.getTotalPages();
            page++;
        } while (page < totalPages);
        return all;
    }

    private void syncProject(Project externalProject) {
        projectRepository.findByExternalProjectId(externalProject.getId())
                .ifPresentOrElse(
                        entity -> entity.syncUpdate(
                                externalProject.getName(),
                                externalProject.getDescription(),
                                externalProject.getClub().getName()),
                        () -> projectRepository.save(buildNewProject(externalProject)));
    }

    private ProjectJpaEntity buildNewProject(Project externalProject) {
        UserJpaEntity owner = findOrCreateLeader(externalProject.getClub().getId());
        return ProjectJpaEntity.builder()
                .user(owner)
                .title(externalProject.getName())
                .description(externalProject.getDescription())
                .affiliation(externalProject.getClub().getName())
                .logo("")
                .prodUrl("")
                .status(Status.APPROVED)
                .repoUrls(new HashSet<>())
                .stackNames(new HashSet<>())
                .externalProjectId(externalProject.getId())
                .build();
    }

    private UserJpaEntity findOrCreateLeader(Long clubId) {
        ClubDetail clubDetail = dataGsmOpenApiClient.clubs().getClub(clubId);
        ParticipantInfo leader = clubDetail.getLeader();
        return userRepository.findByEmail(leader.getEmail())
                .orElseGet(() -> userRepository.save(
                        UserJpaEntity.builder()
                                .email(leader.getEmail())
                                .name(leader.getName())
                                .studentNumber(String.valueOf(leader.getStudentNumber()))
                                .role(Role.USER)
                                .build()));
    }
}
