package team.themoment.everygsm.server.v2.domain.admin.controller;

import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.admin.dto.request.AdminRejectReqDto;
import team.themoment.everygsm.server.v2.domain.admin.service.AdminApproveProjectService;
import team.themoment.everygsm.server.v2.domain.admin.service.AdminQueryProjectService;
import team.themoment.everygsm.server.v2.domain.admin.service.AdminRejectProjectService;
import team.themoment.everygsm.server.v2.domain.project.dto.response.ProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.QueryProjectResDto;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminQueryProjectService adminQueryProjectService;
    private final AdminApproveProjectService adminApproveProjectService;
    private final AdminRejectProjectService adminRejectProjectService;

    @GetMapping("/requests")
    public QueryProjectResDto adminQuery() {
        return adminQueryProjectService.execute();
    }

    @PatchMapping("/approve/{projectId}")
    public ProjectResDto approve(@PathVariable("projectId") Long projectId) {
        return adminApproveProjectService.execute(projectId);
    }

    @PatchMapping("/reject/{projectId}")
    public ProjectResDto reject(@PathVariable("projectId") Long projectId,
            @RequestBody @Valid AdminRejectReqDto reqDto) {
        return adminRejectProjectService.execute(projectId, reqDto);
    }
}
