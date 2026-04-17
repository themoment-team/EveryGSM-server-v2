package team.themoment.everygsm.server.v2.global.thirdparty.feign.datagsm;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import team.themoment.everygsm.server.v2.global.thirdparty.feign.datagsm.config.DatagsmFeignConfig;
import team.themoment.everygsm.server.v2.global.thirdparty.feign.datagsm.dto.ClubListResDto;
import team.themoment.everygsm.server.v2.global.thirdparty.feign.datagsm.dto.DatagsmProjectResDto;
import team.themoment.everygsm.server.v2.global.thirdparty.feign.datagsm.dto.ProjectReqDto;
import team.themoment.everygsm.server.v2.global.thirdparty.feign.datagsm.dto.QueryClubReqDto;
import team.themoment.everygsm.server.v2.global.thirdparty.feign.datagsm.dto.QueryStudentReqDto;
import team.themoment.everygsm.server.v2.global.thirdparty.feign.datagsm.dto.StudentListResDto;

@FeignClient(name = "datagsm-api", url = "${datagsm.api.url}", configuration = DatagsmFeignConfig.class)
public interface DatagsmApiClient {

    @GetMapping("/v1/clubs")
    ClubListResDto getClubs(@SpringQueryMap QueryClubReqDto req);

    @GetMapping("/v1/students")
    StudentListResDto getStudents(@SpringQueryMap QueryStudentReqDto req);

    @PostMapping("/v1/projects")
    DatagsmProjectResDto createProject(@RequestBody ProjectReqDto req);
}
