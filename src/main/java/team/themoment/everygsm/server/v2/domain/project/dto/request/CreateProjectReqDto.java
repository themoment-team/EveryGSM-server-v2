package team.themoment.everygsm.server.v2.domain.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;
import team.themoment.everygsm.server.v2.domain.project.dto.common.RepositoryDto;
import team.themoment.everygsm.server.v2.domain.project.dto.common.TechStackDto;

import java.util.List;

public record CreateProjectReqDto(
        @URL String logo,
        @NotBlank String title,
        @NotBlank String affiliation,
        @NotBlank @Size(max = 200) String description,
        @URL String prodUrl,
        @NotNull List<TechStackDto> techStack,
        @NotNull List<RepositoryDto> repository
) {
}
