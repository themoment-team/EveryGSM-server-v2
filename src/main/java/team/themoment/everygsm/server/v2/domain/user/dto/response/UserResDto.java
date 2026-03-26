package team.themoment.everygsm.server.v2.domain.user.dto.response;

import team.themoment.everygsm.server.v2.domain.user.entity.constant.Role;

public record UserResDto(
        Long id,
        String email,
        String name,
        String studentNumber,
        Role role
) {
}
