package team.themoment.everygsm.server.v2.domain.project.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.themoment.everygsm.server.v2.domain.project.dto.requset.RegisterProjectReqDto;
import team.themoment.everygsm.server.v2.domain.project.service.RegisterProjectService;
import team.themoment.everygsm.server.v2.global.common.response.CommonApiResponse;

@RestController
@RequestMapping("/api/v1/project")
@RequiredArgsConstructor
public class ProjectController {
    private final RegisterProjectService registerProjectService;

    @PostMapping("/registration")
    public CommonApiResponse register(@RequestBody RegisterProjectReqDto reqDto){
        Long userId = 1L;
        registerProjectService.execute(userId, reqDto);
        return CommonApiResponse.success("등록되었습니다.");
    }
}
