package team.themoment.everygsm.server.v2.domain.project.dto.request;

import java.util.List;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import team.themoment.everygsm.server.v2.domain.project.dto.common.TechStackDto;

public record CreateProjectReqDto(@NotBlank String logo, @NotBlank String title, @NotBlank String affiliation,
        @NotBlank @Size(max = 200) String description, @URL String prodUrl, @NotNull List<TechStackDto> techStack,
        @Valid List<@URL String> repository) {
}
