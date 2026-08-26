package team.themoment.everygsm.server.v2.domain.project.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import team.themoment.datagsm.sdk.openapi.DataGsmOpenApiClient;
import team.themoment.datagsm.sdk.openapi.client.ProjectApi;
import team.themoment.datagsm.sdk.openapi.model.Project;
import team.themoment.datagsm.sdk.openapi.model.ProjectResponse;
import team.themoment.everygsm.server.v2.domain.project.entity.ProjectJpaEntity;
import team.themoment.everygsm.server.v2.domain.project.entity.constant.Status;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectRepository;
import team.themoment.everygsm.server.v2.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("프로젝트 동기화 서비스 테스트")
class SyncProjectServiceTest {

    private static final long EXTERNAL_ID = 100L;
    private static final String TITLE = "에브리지즘";
    private static final int START_YEAR = 2026;

    @Mock
    private DataGsmOpenApiClient dataGsmOpenApiClient;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SyncProjectService syncProjectService;

    private Project externalProject() {
        Project project = new Project();
        project.setId(EXTERNAL_ID);
        project.setName(TITLE);
        project.setDescription("설명");
        project.setStartYear(START_YEAR);
        project.setParticipants(List.of());
        return project;
    }

    private ProjectJpaEntity localProject() {
        return ProjectJpaEntity.builder().title(TITLE).description("설명").logo("logo.png").prodUrl("https://a.b")
                .startYear(START_YEAR).status(Status.APPROVED).build();
    }

    @Nested
    @DisplayName("execute 메서드는")
    class Describe_execute {

        @Mock
        private ProjectApi projectApi;

        /** execute()는 @Lazy self 프록시를 통해 호출하므로 자기 자신을 주입해준다. */
        @BeforeEach
        void injectSelf() throws Exception {
            Field self = SyncProjectService.class.getDeclaredField("self");
            self.setAccessible(true);
            self.set(syncProjectService, syncProjectService);
            given(dataGsmOpenApiClient.projects()).willReturn(projectApi);
        }

        @Nested
        @DisplayName("프로젝트 목록을 일부만 가져온 경우")
        class Context_with_partial_fetch {

            @Test
            @DisplayName("미조회 프로젝트를 비활성화하지 않는다")
            void it_skips_deactivation() {
                // 첫 페이지는 정상, 두 번째 페이지는 응답이 비어있는 상황
                ProjectResponse first = new ProjectResponse();
                first.setProjects(List.of(externalProject()));
                first.setTotalPages(2);

                ProjectResponse broken = new ProjectResponse();
                broken.setProjects(null);
                broken.setTotalPages(2);

                given(projectApi.getProjects(any(ProjectApi.ProjectRequest.class))).willReturn(first, broken);

                syncProjectService.execute();

                verify(projectRepository, never()).markUnseenProjectsAsInactive(any());
            }
        }

        @Nested
        @DisplayName("프로젝트 목록 전체를 정상적으로 가져온 경우")
        class Context_with_complete_fetch {

            @Test
            @DisplayName("미조회 프로젝트를 비활성화한다")
            void it_marks_unseen_inactive() {
                ProjectResponse only = new ProjectResponse();
                only.setProjects(List.of(externalProject()));
                only.setTotalPages(1);

                given(projectApi.getProjects(any(ProjectApi.ProjectRequest.class))).willReturn(only);
                given(projectRepository.findByExternalProjectId(EXTERNAL_ID)).willReturn(Optional.of(localProject()));

                syncProjectService.execute();

                verify(projectRepository).markUnseenProjectsAsInactive(Set.of(EXTERNAL_ID));
            }
        }
    }

    @Nested
    @DisplayName("syncProject 메서드는")
    class Describe_syncProject {

        @Nested
        @DisplayName("externalProjectId로 매칭되는 프로젝트가 있는 경우")
        class Context_with_matching_external_id {

            @Test
            @DisplayName("새로 저장하지 않고 기존 프로젝트를 갱신한다")
            void it_updates_existing_project() {
                ProjectJpaEntity existing = localProject();
                given(projectRepository.findByExternalProjectId(EXTERNAL_ID)).willReturn(Optional.of(existing));

                syncProjectService.syncProject(externalProject());

                verify(projectRepository, never()).save(any(ProjectJpaEntity.class));
            }
        }

        @Nested
        @DisplayName("externalProjectId는 없지만 제목과 startYear가 일치하는 프로젝트가 정확히 하나 있는 경우")
        class Context_with_single_orphaned_candidate {

            @Test
            @DisplayName("중복 생성 대신 기존 프로젝트에 externalProjectId를 복구한다")
            void it_heals_instead_of_inserting() {
                ProjectJpaEntity orphan = localProject();
                given(projectRepository.findByExternalProjectId(EXTERNAL_ID)).willReturn(Optional.empty());
                given(projectRepository.findByExternalProjectIdIsNullAndTitleAndStartYearAndStatusNot(TITLE,
                        START_YEAR,
                        Status.INACTIVE)).willReturn(List.of(orphan));

                syncProjectService.syncProject(externalProject());

                assertEquals(EXTERNAL_ID, orphan.getExternalProjectId());
                verify(projectRepository, never()).save(any(ProjectJpaEntity.class));
            }
        }

        @Nested
        @DisplayName("복구 후보가 여러 건인 경우")
        class Context_with_ambiguous_candidates {

            @Test
            @DisplayName("잘못된 복구를 피하고 datagsm 전용 프로젝트로 간주해 무시한다")
            void it_ignores_datagsm_only_project() {
                given(projectRepository.findByExternalProjectId(EXTERNAL_ID)).willReturn(Optional.empty());
                given(projectRepository.findByExternalProjectIdIsNullAndTitleAndStartYearAndStatusNot(TITLE,
                        START_YEAR,
                        Status.INACTIVE)).willReturn(List.of(localProject(), localProject()));

                syncProjectService.syncProject(externalProject());

                verify(projectRepository, never()).save(any(ProjectJpaEntity.class));
            }
        }

        @Nested
        @DisplayName("매칭되는 프로젝트가 전혀 없는 경우")
        class Context_with_no_candidate {

            @Test
            @DisplayName("EveryGSM에 새로 생성하지 않고 무시한다")
            void it_ignores_datagsm_only_project() {
                given(projectRepository.findByExternalProjectId(EXTERNAL_ID)).willReturn(Optional.empty());
                given(projectRepository.findByExternalProjectIdIsNullAndTitleAndStartYearAndStatusNot(TITLE,
                        START_YEAR,
                        Status.INACTIVE)).willReturn(List.of());

                syncProjectService.syncProject(externalProject());

                verify(projectRepository, never()).save(any(ProjectJpaEntity.class));
            }
        }
    }
}
