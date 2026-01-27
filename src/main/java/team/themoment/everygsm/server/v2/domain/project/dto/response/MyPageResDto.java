package team.themoment.everygsm.server.v2.domain.project.dto.response;

import java.util.List;

public record MyPageResDto(List<ProjectResDto> liked, List<ProjectResDto> registered) {
}
