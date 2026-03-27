package team.themoment.everygsm.server.v2.global.client.datagsm;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import team.themoment.everygsm.server.v2.global.client.datagsm.config.DatagsmFeignConfig;
import team.themoment.everygsm.server.v2.global.client.datagsm.dto.ClubListResDto;
import team.themoment.everygsm.server.v2.global.client.datagsm.dto.DatagsmProjectResDto;
import team.themoment.everygsm.server.v2.global.client.datagsm.dto.ProjectReqDto;
import team.themoment.everygsm.server.v2.global.client.datagsm.dto.QueryClubReqDto;
import team.themoment.everygsm.server.v2.global.client.datagsm.dto.QueryStudentReqDto;
import team.themoment.everygsm.server.v2.global.client.datagsm.dto.StudentListResDto;

@FeignClient(name = "datagsm-api", url = "${datagsm.api.url}", configuration = DatagsmFeignConfig.class)
public interface DatagsmApiClient {

    @GetMapping("/v1/clubs")
    ClubListResDto getClubs(@SpringQueryMap QueryClubReqDto req);

    @GetMapping("/v1/students")
    StudentListResDto getStudents(@SpringQueryMap QueryStudentReqDto req);

    @PostMapping("/v1/projects")
    DatagsmProjectResDto createProject(@RequestBody ProjectReqDto req);
}