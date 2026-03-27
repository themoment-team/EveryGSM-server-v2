package team.themoment.everygsm.server.v2.global.client.datagsm.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProjectReqDto {

    private final String name;
    private final String description;
    private final Long clubId;
    private final List<Long> participantIds;
}
