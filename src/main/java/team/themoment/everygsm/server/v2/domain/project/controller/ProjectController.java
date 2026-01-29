package team.themoment.everygsm.server.v2.domain.project.controller;

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
import team.themoment.everygsm.server.v2.domain.project.service.QueryRejectedProjectService;
import team.themoment.everygsm.server.v2.domain.project.service.QueryProjectService;

@RestController
@RequestMapping("/api/v2/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final CreateProjectService createProjectService;
    private final QueryMypageService queryMypageService;
    private final QueryPendingProjectService queryPendingProjectService;
    private final QueryRejectedProjectService queryRejectedProjectService;
    private final QueryProjectService queryProjectService;

    @PostMapping("/registration")
    public ProjectResDto create(@RequestBody @Valid CreateProjectReqDto reqDto) {
        Long userId = 1L; // TODO : 해당 부분 인증 구현되면 변경해야 합니다
        return createProjectService.execute(userId, reqDto);
    }

    @GetMapping("/my")
    public MyPageResDto query() {
        Long userId = 1L; // TODO : 해당 부분 인증 구현되면 변경해야 합니다
        return queryMypageService.execute(userId);
    }

    @GetMapping("/my/pending")
    public ProjectListResDto pending() {
        Long userId = 1L; // TODO : 해당 부분 인증 구현되면 변경해야 합니다
        return queryPendingProjectService.execute(userId);
    }

    @GetMapping("/my/rejected")
    public ProjectListResDto rejected() {
        Long userId = 1L; // TODO : 해당 부분 인증 구현되면 변경해야 합니다
        return queryRejectedProjectService.execute(userId);
    @GetMapping
    public QueryProjectResDto query() {
        Long userId = 1L; // TODO : 해당 부분 인증 구현되면 변경해야 합니다
        return queryProjectService.execute(userId);
    }
}
