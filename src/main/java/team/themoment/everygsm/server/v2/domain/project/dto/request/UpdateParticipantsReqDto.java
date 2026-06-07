package team.themoment.everygsm.server.v2.domain.project.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateParticipantsReqDto(@NotNull @Valid @Size(max = 50) List<@NotNull Long> participantIds) {
}
