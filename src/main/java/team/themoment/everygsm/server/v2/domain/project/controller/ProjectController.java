package team.themoment.everygsm.server.v2.domain.project.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.project.dto.request.CreateProjectReqDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.MyPageResDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.ProjectListResDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.ProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.QueryProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.service.CreateProjectService;
import team.themoment.everygsm.server.v2.domain.project.service.QueryMypageService;
import team.themoment.everygsm.server.v2.domain.project.service.QueryPendingProjectService;
import team.themoment.everygsm.server.v2.domain.project.service.QueryProjectService;
import team.themoment.everygsm.server.v2.domain.project.service.QueryRejectedProjectService;

@RestController
@RequestMapping("/api/v2/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final CreateProjectService createProjectService;
    private final QueryMypageService queryMypageService;
    private final QueryPendingProjectService queryPendingProjectService;
    private final QueryRejectedProjectService queryRejectedProjectService;
    private final QueryProjectService queryProjectService;

    @GetMapping("/my")
    public MyPageResDto queryMyProjects(@AuthenticationPrincipal Long userId) {
        return queryMypageService.execute(userId);
    }

    @GetMapping("/my/pending")
    public ProjectListResDto pending(@AuthenticationPrincipal Long userId) {
        return queryPendingProjectService.execute(userId);
    }

    @GetMapping("/my/rejected")
    public ProjectListResDto rejected(@AuthenticationPrincipal Long userId) {
        return queryRejectedProjectService.execute(userId);
    }

    @PostMapping("/registration")
    public ProjectResDto create(@AuthenticationPrincipal Long userId, @RequestBody @Valid CreateProjectReqDto reqDto) {
        return createProjectService.execute(userId, reqDto);
    }

    @GetMapping
    public QueryProjectResDto query(@AuthenticationPrincipal Long userId) {
        return queryProjectService.execute(userId);
    }
}
