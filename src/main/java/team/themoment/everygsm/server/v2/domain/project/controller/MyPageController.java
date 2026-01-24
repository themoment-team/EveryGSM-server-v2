package team.themoment.everygsm.server.v2.domain.project.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.project.dto.response.MyPageResDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.ProjectListResDto;
import team.themoment.everygsm.server.v2.domain.project.service.QueryMypageService;
import team.themoment.everygsm.server.v2.domain.project.service.QueryPendingProjectService;
import team.themoment.everygsm.server.v2.domain.project.service.QueryRejectedProjectService;

@RestController
@RequestMapping("/api/v1/mypage")
@RequiredArgsConstructor
public class MyPageController {
    private final QueryMypageService queryMypageService;
    private final QueryPendingProjectService queryPendingProjectService;
    private final QueryRejectedProjectService queryRejectedProjectService;

    @GetMapping
    public MyPageResDto query() {
        Long userId = 1L; // TODO : 해당 부분 인증 구현되면 변경해야 합니다
        return queryMypageService.execute(userId);
    }

    @GetMapping("/pending")
    public ProjectListResDto pending() {
        Long userId = 1L; // TODO : 해당 부분 인증 구현되면 변경해야 합니다
        return queryPendingProjectService.execute(userId);
    }

    @GetMapping("/rejected")
    public ProjectListResDto rejected() {
        Long userId = 1L; // TODO : 해당 부분 인증 구현되면 변경해야 합니다
        return queryRejectedProjectService.execute(userId);
    }
}
