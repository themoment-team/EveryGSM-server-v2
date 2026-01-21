package team.themoment.everygsm.server.v2.domain.project.dto.response;

import team.themoment.everygsm.server.v2.domain.project.dto.common.RepositoryDto;
import team.themoment.everygsm.server.v2.domain.project.dto.common.TechStackDto;
import team.themoment.everygsm.server.v2.domain.project.entity.constant.Status;

import java.time.LocalDateTime;
import java.util.List;

public record CreateProjectResDto(
        Long projectId,
        String logo,
        String title,
        String affiliation,
        String description,
        String prodUrl,
        Status status,
        String reason,
        LocalDateTime createdAt,
        List<TechStackDto> techStack,
        List<RepositoryDto> repository,
        boolean liked
) {
}
