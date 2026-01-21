package team.themoment.everygsm.server.v2.domain.project.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.themoment.everygsm.server.v2.domain.project.dto.request.CreateProjectReqDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.CreateProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.service.CreateProjectService;

@RestController
@RequestMapping("/api/v1/project")
@RequiredArgsConstructor
public class ProjectController {
    private final CreateProjectService createProjectService;

    @PostMapping("/registration")
    public CreateProjectResDto create(@RequestBody @Valid CreateProjectReqDto reqDto){
        Long userId = 1L; // TODO : 해당 부분 인증 구현되면 변경해야 합니다
        return createProjectService.execute(userId, reqDto);
    }
}
