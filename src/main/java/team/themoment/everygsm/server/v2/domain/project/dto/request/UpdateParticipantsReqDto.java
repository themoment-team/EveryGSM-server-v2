package team.themoment.everygsm.server.v2.domain.project.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record UpdateParticipantsReqDto(@NotNull @Valid List<@NotNull Long> participantIds) {
}
