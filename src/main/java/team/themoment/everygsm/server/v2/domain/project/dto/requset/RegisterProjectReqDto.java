package team.themoment.everygsm.server.v2.domain.project.dto.requset;

import team.themoment.everygsm.server.v2.domain.project.dto.common.RepositoryDto;
import team.themoment.everygsm.server.v2.domain.project.dto.common.TechStackDto;

import java.util.List;

public record RegisterProjectReqDto(
        String logo,
        String title,
        String affiliation,
        String description,
        String prodUrl,
        List<TechStackDto> techStack,
        List<RepositoryDto> repository
) {
}
