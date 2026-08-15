package team.themoment.everygsm.server.v2.domain.admin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

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
import team.themoment.everygsm.server.v2.domain.project.mapper.ProjectMapper;
import team.themoment.everygsm.server.v2.domain.project.repository.ProjectRepository;
import team.themoment.everygsm.server.v2.global.exception.error.ExpectedException;
import team.themoment.everygsm.server.v2.global.thirdparty.feign.datagsm.DatagsmApiClient;
import team.themoment.everygsm.server.v2.global.thirdparty.feign.datagsm.dto.ProjectReqDto;

@ExtendWith(MockitoExtension.class)
@DisplayName("어드민 프로젝트 승인 서비스 테스트")
class AdminApproveProjectServiceTest {

    private static final long PROJECT_ID = 1L;
    private static final long EXTERNAL_ID = 100L;
    private static final String TITLE = "에브리지즘";
    private static final int START_YEAR = 2026;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private DatagsmApiClient datagsmApiClient;

    @Mock
    private DataGsmOpenApiClient dataGsmOpenApiClient;

    @Mock
    private ProjectApi projectApi;

    @InjectMocks
    private AdminApproveProjectService adminApproveProjectService;

    private ProjectJpaEntity project;

    @BeforeEach
    void setUp() {
        project = ProjectJpaEntity.builder().title(TITLE).description("설명").logo("logo.png").prodUrl("https://a.b")
                .startYear(START_YEAR).status(Status.PENDING).build();
        given(projectRepository.findProjectWithCollectionsById(PROJECT_ID)).willReturn(Optional.of(project));
        given(dataGsmOpenApiClient.projects()).willReturn(projectApi);
    }

    /** datagsm 프로젝트 검색 결과를 흉내낸다. */
    private void givenDatagsmSearchReturns(Project... found) {
        ProjectResponse response = new ProjectResponse();
        response.setProjects(List.of(found));
        response.setTotalPages(1);
        given(projectApi.getProjects(any(ProjectApi.ProjectRequest.class))).willReturn(response);
    }

    private Project datagsmProject() {
        Project external = new Project();
        external.setId(EXTERNAL_ID);
        external.setName(TITLE);
        external.setStartYear(START_YEAR);
        return external;
    }

    @Nested
    @DisplayName("execute 메서드는")
    class Describe_execute {

        @Nested
        @DisplayName("datagsm 등록 응답을 읽지 못했지만 같은 이름의 프로젝트가 datagsm에 이미 있는 경우")
        class Context_with_unreadable_response_but_existing_project {

            @Test
            @DisplayName("externalProjectId를 회수해 유실을 막는다")
            void it_recovers_external_project_id() {
                // 최초 조회는 미등록, 생성 응답은 읽지 못하고, 재조회에서 발견되는 상황
                ProjectResponse empty = new ProjectResponse();
                empty.setProjects(List.of());
                empty.setTotalPages(1);

                ProjectResponse found = new ProjectResponse();
                found.setProjects(List.of(datagsmProject()));
                found.setTotalPages(1);

                given(projectApi.getProjects(any(ProjectApi.ProjectRequest.class))).willReturn(empty, found);
                given(datagsmApiClient.createProject(any(ProjectReqDto.class))).willReturn(null);
                given(projectRepository.findByExternalProjectId(EXTERNAL_ID)).willReturn(Optional.empty());

                adminApproveProjectService.execute(PROJECT_ID);

                assertEquals(EXTERNAL_ID, project.getExternalProjectId());
                assertEquals(Status.APPROVED, project.getStatus());
            }
        }

        @Nested
        @DisplayName("datagsm에 같은 이름과 startYear의 프로젝트가 이미 있는 경우")
        class Context_with_existing_datagsm_project {

            @Test
            @DisplayName("새로 생성하지 않고 기존 id에 매핑한다")
            void it_maps_without_creating() {
                givenDatagsmSearchReturns(datagsmProject());
                given(projectRepository.findByExternalProjectId(EXTERNAL_ID)).willReturn(Optional.empty());

                adminApproveProjectService.execute(PROJECT_ID);

                assertEquals(EXTERNAL_ID, project.getExternalProjectId());
                verify(datagsmApiClient, never()).createProject(any(ProjectReqDto.class));
            }
        }

        @Nested
        @DisplayName("매핑하려는 externalProjectId를 다른 프로젝트가 이미 점유한 경우")
        class Context_with_occupied_external_id {

            @Test
            @DisplayName("ExpectedException을 던진다")
            void it_throws_expected_exception() {
                givenDatagsmSearchReturns(datagsmProject());
                ProjectJpaEntity occupier = mock(ProjectJpaEntity.class);
                given(occupier.getId()).willReturn(999L);
                given(projectRepository.findByExternalProjectId(EXTERNAL_ID)).willReturn(Optional.of(occupier));

                assertThrows(ExpectedException.class, () -> adminApproveProjectService.execute(PROJECT_ID));
            }
        }
    }
}
