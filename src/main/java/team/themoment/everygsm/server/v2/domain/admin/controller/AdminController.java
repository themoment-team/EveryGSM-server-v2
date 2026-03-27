package team.themoment.everygsm.server.v2.domain.admin.controller;

import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.admin.dto.request.AdminRejectReqDto;
import team.themoment.everygsm.server.v2.domain.admin.service.AdminApproveProjectService;
import team.themoment.everygsm.server.v2.domain.admin.service.AdminQueryProjectService;
import team.themoment.everygsm.server.v2.domain.admin.service.AdminQueryPendingProjectService;
import team.themoment.everygsm.server.v2.domain.admin.service.AdminRejectProjectService;
import team.themoment.everygsm.server.v2.domain.project.dto.response.ProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.QueryProjectResDto;

@Tag(name = "Admin", description = "관리자 API")
@RestController
@RequestMapping("/api/v2/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminQueryProjectService adminQueryProjectService;
    private final AdminQueryPendingProjectService adminQueryPendingProjectService;
    private final AdminApproveProjectService adminApproveProjectService;
    private final AdminRejectProjectService adminRejectProjectService;

    @Operation(summary = "승인 대기 프로젝트 조회", description = "승인 대기 중인 모든 프로젝트를 조회합니다")
    @GetMapping("/requests")
    public QueryProjectResDto adminQuery() {
        return adminQueryProjectService.execute();
    }

    @Operation(summary = "승인 대기 프로젝트 단건 조회", description = "승인 대기 중인 프로젝트를 ID로 조회합니다")
    @GetMapping("/requests/{id}")
    public ProjectResDto adminQueryPendingProject(@Parameter(description = "프로젝트 ID") @PathVariable Long id) {
        return adminQueryPendingProjectService.execute(id);
    }

    @Operation(summary = "프로젝트 승인", description = "프로젝트를 승인합니다")
    @PatchMapping("/approve/{projectId}")
    public ProjectResDto approve(@Parameter(description = "프로젝트 ID") @PathVariable("projectId") Long projectId) {
        return adminApproveProjectService.execute(projectId);
    }

    @Operation(summary = "프로젝트 거절", description = "프로젝트를 거절 사유와 함께 거절합니다")
    @PatchMapping("/reject/{projectId}")
    public ProjectResDto reject(@Parameter(description = "프로젝트 ID") @PathVariable("projectId") Long projectId,
            @RequestBody @Valid AdminRejectReqDto reqDto) {
        return adminRejectProjectService.execute(projectId, reqDto);
    }
}
