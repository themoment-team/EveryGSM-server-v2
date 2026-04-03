package team.themoment.everygsm.server.v2.global.thirdparty.feign.datagsm.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QueryClubReqDto {

    private final String clubName;

    @Builder.Default
    private final int page = 0;

    @Builder.Default
    private final int size = 1;
}
