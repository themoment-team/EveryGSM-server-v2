package team.themoment.everygsm.server.v2.domain.project.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import team.themoment.everygsm.server.v2.domain.project.dto.response.TestResDto;

@Tag(name = "Test", description = "개발 환경 전용 테스트 API")
@Slf4j
@Profile("stage")
@RestController
@RequestMapping("/api/v2/test")
@RequiredArgsConstructor
public class TestController {

    @Operation(summary = "요청 바디 로그 출력", description = "stage 프로파일에서만 활성화됩니다. 전달된 요청 바디를 그대로 로그로 출력합니다.")
    @PostMapping
    public TestResDto test(@RequestBody Object body) {
        log.info("[TEST] request body={}", body);
        return new TestResDto("ok", null);
    }
}
