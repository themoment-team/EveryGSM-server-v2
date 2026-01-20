package team.themoment.everygsm.server.v2.domain.project.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.themoment.everygsm.server.v2.domain.project.dto.request.RegisterProjectReqDto;
import team.themoment.everygsm.server.v2.domain.project.dto.response.RegisterProjectResDto;
import team.themoment.everygsm.server.v2.domain.project.service.RegisterProjectService;

@RestController
@RequestMapping("/api/v1/project")
@RequiredArgsConstructor
public class ProjectController {
    private final RegisterProjectService registerProjectService;

    @PostMapping("/registration")
    public RegisterProjectResDto register(@RequestBody @Valid RegisterProjectReqDto reqDto){
        Long userId = 1L;
        return registerProjectService.execute(userId, reqDto);
    }
}
