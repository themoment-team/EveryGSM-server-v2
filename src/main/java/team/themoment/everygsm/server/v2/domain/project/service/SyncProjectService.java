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
            } catch (RuntimeException e) {
                log.error("Failed to sync project id={}", externalProject.getId(), e);
            }
        }

        projectRepository.markUnseenProjectsAsInactive(seenExternalIds);
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
        String affiliation = externalProject.getClub() != null ? externalProject.getClub().getName() : null;
        UserJpaEntity owner = findOrCreateOwner(externalProject);
        projectRepository.findByExternalProjectId(externalProject.getId())
                .ifPresentOrElse(entity -> entity
                        .syncUpdate(externalProject.getName(), externalProject.getDescription(), affiliation, owner),
                        () -> projectRepository.save(buildNewProject(externalProject, affiliation, owner)));
    }

    private ProjectJpaEntity buildNewProject(Project externalProject, String affiliation, UserJpaEntity owner) {
        return ProjectJpaEntity.builder().user(owner).title(externalProject.getName())
                .description(externalProject.getDescription()).affiliation(affiliation).logo("").prodUrl("")
                .status(Status.APPROVED).stackNames(new HashSet<>())
                .externalProjectId(externalProject.getId()).build();
    }

    private UserJpaEntity findOrCreateOwner(Project externalProject) {
        if (externalProject.getClub() != null) {
            try {
                ClubDetail clubDetail = dataGsmOpenApiClient.clubs().getClub(externalProject.getClub().getId());
                return findOrCreateUser(clubDetail.getLeader());
            } catch (RuntimeException e) {
                log.warn("Failed to fetch club leader for clubId={}, falling back to project participants",
                        externalProject.getClub().getId(),
                        e);
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
