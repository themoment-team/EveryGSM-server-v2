package team.themoment.everygsm.server.v2.domain.project.controller;

import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.project.dto.request.CreateProjectReqDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.ProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.service.CreateProjectService;

@RestController
@RequestMapping("/api/v1/project")
@RequiredArgsConstructor
public class ProjectController {
    private final CreateProjectService createProjectService;

    @PostMapping("/registration")
    public ProjectResDto create(@RequestBody @Valid CreateProjectReqDto reqDto) {
        Long userId = 1L; // TODO : 해당 부분 인증 구현되면 변경해야 합니다
        return createProjectService.execute(userId, reqDto);
    }
}
