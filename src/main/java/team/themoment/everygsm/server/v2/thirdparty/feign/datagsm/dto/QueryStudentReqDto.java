package team.themoment.everygsm.server.v2.thirdparty.feign.datagsm.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QueryStudentReqDto {

    private final String email;

    @Builder.Default
    private final int page = 0;

    @Builder.Default
    private final int size = 1;
}
