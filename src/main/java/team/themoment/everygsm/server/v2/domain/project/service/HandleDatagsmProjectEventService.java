package team.themoment.everygsm.server.v2.domain.project.service;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import team.themoment.everygsm.server.v2.domain.project.dto.webhook.DatagsmEventReqDto;
import team.themoment.everygsm.server.v2.domain.project.dto.webhook.DatagsmProjectEventObjectDto;
import team.themoment.everygsm.server.v2.domain.project.dto.webhook.DatagsmProjectEventParticipantDto;
import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectRepository;
import team.themoment.everygsm.server.v2.domain.user.entity.UserJpaEntity;
import team.themoment.everygsm.server.v2.domain.user.entity.constant.Role;
import team.themoment.everygsm.server.v2.domain.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class HandleDatagsmProjectEventService {

    private static final String PROJECT_UPDATED_EVENT = "project.updated";

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Transactional
    public void execute(DatagsmEventReqDto event) {
        if (!PROJECT_UPDATED_EVENT.equals(event.event())) {
            log.info("처리 대상이 아닌 datagsm 이벤트를 무시합니다. event={}", event.event());
            return;
        }

        if (event.data() == null) {
            log.warn("project.updated 이벤트에 data 필드가 없어 무시합니다. eventId={}", event.id());
            return;
        }

        DatagsmProjectEventObjectDto newState = event.data().newSnapshot() != null
                ? event.data().newSnapshot().object()
                : null;
        if (newState == null || newState.id() == null) {
            log.warn("project.updated 이벤트에 유효한 new 스냅샷이 없어 무시합니다. eventId={}", event.id());
            return;
        }

        ProjectJpaEntity project = projectRepository.findByExternalProjectId(newState.id()).orElse(null);
        if (project == null) {
            log.info("EveryGSM에 매핑되지 않은 datagsm 프로젝트 이벤트라 무시합니다. externalProjectId={}", newState.id());
            return;
        }

        if (isSameAsCurrent(project, newState)) {
            log.info("EveryGSM이 유발한 변경과 동일해 이벤트를 스킵합니다. externalProjectId={}", newState.id());
            return;
        }

        Set<UserJpaEntity> participants = resolveParticipants(newState);
        UserJpaEntity owner = resolveOwner(participants, project.getUser());
        project.syncUpdate(newState.name(),
                newState.description(),
                newState.club() != null ? newState.club().name() : null,
                newState.startYear(),
                owner);
        project.replaceParticipants(participants);
        log.info("datagsm project.updated 이벤트를 반영했습니다. externalProjectId={}", newState.id());
    }

    private boolean isSameAsCurrent(ProjectJpaEntity project, DatagsmProjectEventObjectDto newState) {
        String currentAffiliation = project.getAffiliation();
        String newAffiliation = newState.club() != null ? newState.club().name() : null;

        Set<String> currentParticipantEmails = project.getParticipants().stream().map(UserJpaEntity::getEmail)
                .collect(Collectors.toSet());
        Set<String> newParticipantEmails = newState.participants() == null
                ? Set.of()
                : newState.participants().stream().map(DatagsmProjectEventParticipantDto::email)
                        .collect(Collectors.toSet());

        return Objects.equals(project.getTitle(), newState.name())
                && Objects.equals(project.getDescription(), newState.description())
                && Objects.equals(currentAffiliation, newAffiliation)
                && Objects.equals(project.getStartYear(), newState.startYear())
                && Objects.equals(currentParticipantEmails, newParticipantEmails);
    }

    private Set<UserJpaEntity> resolveParticipants(DatagsmProjectEventObjectDto newState) {
        if (newState.participants() == null) {
            return new LinkedHashSet<>();
        }

        Set<UserJpaEntity> participants = new LinkedHashSet<>();
        for (DatagsmProjectEventParticipantDto participant : newState.participants()) {
            if (participant.email() == null || participant.email().isBlank()) {
                continue;
            }
            participants.add(userRepository.findByEmail(participant.email())
                    .orElseGet(() -> userRepository
                            .save(UserJpaEntity.builder().email(participant.email()).name(participant.name())
                                    .studentNumber(participant.studentNumber()).role(Role.USER).build())));
        }
        return participants;
    }

    private UserJpaEntity resolveOwner(Set<UserJpaEntity> participants, UserJpaEntity fallback) {
        return participants.isEmpty() ? fallback : participants.iterator().next();
    }
}
