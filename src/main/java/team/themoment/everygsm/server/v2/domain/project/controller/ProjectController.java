package team.themoment.everygsm.server.v2.domain.project.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.project.dto.request.CreateProjectReqDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.MyPageResDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.ProjectListResDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.ProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.QueryProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.service.CreateProjectLikeService;
import team.themoment.everygsm.server.v2.domain.project.service.CreateProjectService;
import team.themoment.everygsm.server.v2.domain.project.service.DeleteProjectLikeService;
import team.themoment.everygsm.server.v2.domain.project.service.DeleteProjectService;
import team.themoment.everygsm.server.v2.domain.project.service.QueryMyProjectService;
import team.themoment.everygsm.server.v2.domain.project.service.QueryMypageService;
import team.themoment.everygsm.server.v2.domain.project.service.QueryPendingProjectService;
import team.themoment.everygsm.server.v2.domain.project.service.QueryProjectService;
import team.themoment.everygsm.server.v2.domain.project.service.QueryRejectedProjectService;

@Tag(name = "Project", description = "프로젝트 API")
@RestController
@RequestMapping("/api/v2/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final CreateProjectService createProjectService;
    private final QueryMypageService queryMypageService;
    private final QueryMyProjectService queryMyProjectService;
    private final QueryPendingProjectService queryPendingProjectService;
    private final QueryRejectedProjectService queryRejectedProjectService;
    private final QueryProjectService queryProjectService;
    private final DeleteProjectService deleteProjectService;
    private final CreateProjectLikeService createProjectLikeService;
    private final DeleteProjectLikeService deleteProjectLikeService;

    @Operation(summary = "마이페이지 조회", description = "좋아요한 프로젝트와 등록한 프로젝트를 조회합니다")
    @GetMapping("/my")
    public MyPageResDto queryMyProjects(@AuthenticationPrincipal Long userId) {
        return queryMypageService.execute(userId);
    }

    @Operation(summary = "내 프로젝트 단건 조회", description = "자신이 등록한 프로젝트를 ID로 조회합니다")
    @GetMapping("/my/{id}")
    public ProjectResDto queryMyProject(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        return queryMyProjectService.execute(userId, id);
    }

    @Operation(summary = "승인 대기 프로젝트 조회", description = "내가 등록한 승인 대기 중인 프로젝트 목록을 조회합니다")
    @GetMapping("/my/pending")
    public ProjectListResDto pending(@AuthenticationPrincipal Long userId) {
        return queryPendingProjectService.execute(userId);
    }

    @Operation(summary = "거절된 프로젝트 조회", description = "내가 등록한 거절된 프로젝트 목록을 조회합니다")
    @GetMapping("/my/rejected")
    public ProjectListResDto rejected(@AuthenticationPrincipal Long userId) {
        return queryRejectedProjectService.execute(userId);
    }

    @Operation(summary = "프로젝트 등록", description = "새로운 프로젝트를 등록합니다")
    @PostMapping("/registration")
    public ProjectResDto create(@AuthenticationPrincipal Long userId, @RequestBody @Valid CreateProjectReqDto reqDto) {
        return createProjectService.execute(userId, reqDto);
    }

    @Operation(summary = "승인된 프로젝트 전체 조회", description = "승인된 모든 프로젝트를 조회합니다")
    @GetMapping
    public QueryProjectResDto query(@AuthenticationPrincipal Long userId) {
        return queryProjectService.execute(userId);
    }

    @Operation(summary = "프로젝트 삭제", description = "자신이 등록한 프로젝트를 삭제합니다")
    @DeleteMapping("/my/{projectId}")
    public void delete(@AuthenticationPrincipal Long userId, @PathVariable Long projectId) {
        deleteProjectService.execute(userId, projectId);
    }

    @Operation(summary = "프로젝트 좋아요", description = "프로젝트에 좋아요를 추가합니다")
    @PostMapping("/like/{projectId}")
    public ProjectResDto createLike(@AuthenticationPrincipal Long userId, @PathVariable Long projectId) {
        return createProjectLikeService.execute(userId, projectId);
    }

    @Operation(summary = "프로젝트 좋아요 취소", description = "프로젝트 좋아요를 취소합니다")
    @DeleteMapping("/like/{projectId}")
    public ProjectResDto deleteLike(@AuthenticationPrincipal Long userId, @PathVariable Long projectId) {
        return deleteProjectLikeService.execute(userId, projectId);
    }
}
