package team.themoment.everygsm.server.v2.domain.project.dto.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DatagsmEventDataDto(@JsonProperty("old") DatagsmEventSnapshotDto oldSnapshot,
        @JsonProperty("new") DatagsmEventSnapshotDto newSnapshot) {
}
