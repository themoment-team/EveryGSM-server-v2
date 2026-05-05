package team.themoment.everygsm.server.v2.domain.project.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import team.themoment.everygsm.server.v2.domain.project.dto.common.RepositoryDto;
import team.themoment.everygsm.server.v2.domain.project.dto.common.TechStackDto;
import team.themoment.everygsm.server.v2.domain.project.entity.constant.Status;

public record ProjectResDto(Long projectId, String logo, String title, String affiliation, String description,
        String prodUrl, Integer startYear, Status status, String reason, LocalDateTime createdAt,
        List<TechStackDto> techStack, List<RepositoryDto> repository, boolean liked) {
}
